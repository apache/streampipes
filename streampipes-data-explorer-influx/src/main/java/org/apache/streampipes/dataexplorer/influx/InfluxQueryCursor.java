/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryBatch;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;

import com.squareup.moshi.JsonAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSource;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Adapts the client's chunk callback to a bounded, cancellable, single-consumer cursor. */
public final class InfluxQueryCursor implements DatasetQueryCursor {
  private static final Object END = new Object();
  private final Runnable release;
  private final boolean ownsDecodedRows;
  private final QueryTimestampFormat timestampFormat;
  private final ArrayBlockingQueue<Object> batches = new ArrayBlockingQueue<>(1);
  private final AtomicBoolean closed = new AtomicBoolean();
  private volatile InfluxDB.Cancellable cancellable;
  private Object next;

  InfluxQueryCursor(InfluxDB client, Query query) {
    this(client, query, client::close);
  }

  /** The release hook defines client ownership and must cancel owned HTTP calls before closing the client. */
  public InfluxQueryCursor(InfluxDB client, Query query, Runnable release) {
    this(client, query, QueryTimestampFormat.EPOCH_MILLIS, release);
  }

  public InfluxQueryCursor(InfluxDB client, Query query, QueryTimestampFormat format, Runnable release) {
    this.release = release;
    this.timestampFormat = java.util.Objects.requireNonNull(format);
    this.ownsDecodedRows = false;
    try {
      client.query(query, 1000, this::receive, () -> publish(END), this::fail);
    } catch (RuntimeException e) {
      close();
      throw e;
    }
  }

  InfluxQueryCursor(Call call, JsonAdapter<QueryResult> decoder, Consumer<InfluxQueryCursor> release) {
    this(call, decoder, QueryTimestampFormat.EPOCH_MILLIS, release);
  }

  InfluxQueryCursor(Call call, JsonAdapter<QueryResult> decoder, QueryTimestampFormat format,
                    Consumer<InfluxQueryCursor> release) {
    this.release = () -> release.accept(this);
    this.timestampFormat = format;
    this.ownsDecodedRows = true;
    this.cancellable = new InfluxDB.Cancellable() {
      @Override
      public void cancel() {
        call.cancel();
      }

      @Override
      public boolean isCanceled() {
        return call.isCanceled();
      }
    };
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call failedCall, IOException error) {
        fail(error);
      }

      @Override
      public void onResponse(Call completedCall, Response response) {
        try (response) {
          if (!response.isSuccessful() || response.body() == null) {
            throw new QueryExecutionException("Influx query failed with HTTP " + response.code());
          }
          var source = response.body().source();
          while (!closed.get() && !completedCall.isCanceled() && hasNextDocument(source)) {
            var result = decoder.fromJson(source);
            if (result != null) {
              receive(cancellable, result);
            }
          }
        } catch (Exception e) {
          fail(e);
          return;
        }
        // Return the HTTP connection to the pool before signalling completion to the consumer.
        publish(END);
      }
    });
  }

  private static boolean hasNextDocument(BufferedSource source) throws IOException {
    // Skip separators before decoding, so EOF between chunks is distinct from truncated JSON within a chunk.
    while (source.request(1)) {
      byte next = source.getBuffer().getByte(0);
      if (next != ' ' && next != '\n' && next != '\r' && next != '\t') {
        return true;
      }
      source.skip(1);
    }
    return false;
  }

  private void receive(InfluxDB.Cancellable cancellation, QueryResult result) {
    cancellable = cancellation;
    if (closed.get()) {
      cancellation.cancel();
      return;
    }
    // The Java client's JSON chunk reader reports EOF using a synthetic error before onComplete.
    if ("DONE".equals(result.getError()) && result.getResults() == null) {
      return;
    }
    if (result.hasError()) {
      fail(new QueryExecutionException(result.getError()));
      cancellation.cancel();
      return;
    }
    if (result.getResults() == null) {
      return;
    }
    for (var statement : result.getResults()) {
      if (statement.hasError()) {
        fail(new QueryExecutionException(statement.getError()));
        cancellation.cancel();
        return;
      }
      if (statement.getSeries() != null) {
        for (var series : statement.getSeries()) {
          var rows = new ArrayList<List<Object>>();
          int time = series.getColumns().indexOf("time");
          for (var nativeRow : series.getValues() == null ? List.<List<Object>>of() : series.getValues()) {
            // Our decoder creates fresh mutable rows and never retains or reuses them.
            // Borrowed native clients keep the defensive-copy contract.
            var row = ownsDecodedRows ? nativeRow : new ArrayList<>(nativeRow);
            if (time >= 0 && row.get(time) != null && timestampFormat != QueryTimestampFormat.RFC3339) {
              // Borrowed clients may return RFC3339 even when the cursor is configured for a numeric format.
              Object value = row.get(time);
              row.set(time, value instanceof Number number ? number.longValue()
                  : timestampFormat.convert(value, QueryTimestampFormat.RFC3339));
            }
            rows.add(row);
          }
          publish(QueryBatch.takeOwnership(series.getColumns(), series.getTags(), rows));
        }
      }
    }
  }

  private void fail(Throwable error) {
    publish(new QueryExecutionException("Influx query failed", error));
  }

  private void publish(Object value) {
    try {
      while (!closed.get() && !batches.offer(value, 50, TimeUnit.MILLISECONDS)) {
        // Backpressure: never accumulate the full export in the client callback.
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      close();
    }
  }

  @Override
  public QueryTimestampFormat timestampFormat() {
    return timestampFormat;
  }

  @Override
  public boolean hasNext() {
    if (closed.get()) {
      return false;
    }
    if (next == null) {
      try {
        next = batches.take();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        close();
        throw new QueryExecutionException("Interrupted while reading Influx results", e);
      }
    }
    if (next instanceof RuntimeException error) {
      close();
      throw error;
    }
    if (next == END) {
      close();
      return false;
    }
    return true;
  }

  @Override
  public QueryBatch next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    var result = (QueryBatch) next;
    next = null;
    return result;
  }

  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      var active = cancellable;
      if (active != null) {
        active.cancel();
      }
      batches.clear();
      batches.offer(END);
      release.run();
    }
  }
}
