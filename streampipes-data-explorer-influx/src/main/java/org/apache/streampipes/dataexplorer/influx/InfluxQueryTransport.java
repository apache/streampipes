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

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.dataexplorer.influx.auth.InfluxAuthMode;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import com.squareup.moshi.JsonAdapter;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** Application-owned InfluxQL transport. Cursors own calls; only shutdown disposes the connection pool. */
public final class InfluxQueryTransport implements AutoCloseable {
  private final OkHttpClient client;
  private final HttpUrl endpoint;
  private final String database;
  private final JsonAdapter<QueryResult> decoder = InfluxQueryResultDecoder.create(QueryTimestampFormat.EPOCH_MILLIS);
  private final JsonAdapter<QueryResult> exactDecoder = InfluxQueryResultDecoder.create(QueryTimestampFormat.EPOCH_NANOS);
  private final Set<InfluxQueryCursor> cursors = new HashSet<>();
  private final Set<Call> calls = new HashSet<>();
  private boolean closed;

  public InfluxQueryTransport(InfluxConnectionSettings settings) {
    this(settings, Duration.ofSeconds(120));
  }

  public InfluxQueryTransport(InfluxConnectionSettings settings, Duration timeout) {
    database = settings.getDatabaseName();
    endpoint = HttpUrl.get(settings.getConnectionUrl()).newBuilder().addPathSegment("query").build();
    var dispatcher = new Dispatcher();
    // Streaming callbacks can remain active while consumers process batches. Avoid the default five-call host limit.
    dispatcher.setMaxRequestsPerHost(dispatcher.getMaxRequests());
    String authorization = settings.getAuthMode() == InfluxAuthMode.TOKEN
        ? "Token " + settings.getToken() : Credentials.basic(settings.getUsername(), settings.getPassword());
    client = new OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
        .connectTimeout(timeout)
        .readTimeout(timeout)
        .writeTimeout(timeout)
        .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
            .header("Authorization", authorization).build()))
        .build();
  }

  public synchronized InfluxQueryCursor open(Query query) {
    return open(query, QueryTimestampFormat.EPOCH_MILLIS);
  }

  public synchronized InfluxQueryCursor open(Query query, QueryTimestampFormat requestedFormat) {
    ensureOpen();
    var format = InfluxTimestampEncoding.supported(java.util.Objects.requireNonNull(requestedFormat));
    var selectedDecoder = format == QueryTimestampFormat.EPOCH_NANOS || format == QueryTimestampFormat.EPOCH_MICROS
        ? exactDecoder : decoder;
    var cursor = new InfluxQueryCursor(client.newCall(request(query, true, format)), selectedDecoder,
        format, this::release);
    cursors.add(cursor);
    return cursor;
  }

  /** Small metadata queries use the same pool without holding a streaming cursor. */
  public QueryResult execute(Query query) {
    Call call;
    synchronized (this) {
      ensureOpen();
      call = client.newCall(request(query, false, QueryTimestampFormat.EPOCH_MILLIS));
      calls.add(call);
    }
    try (var response = call.execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        throw new QueryExecutionException("Influx query failed with HTTP " + response.code());
      }
      var result = decoder.fromJson(response.body().source());
      if (result == null) {
        throw new QueryExecutionException("Influx returned an empty query response");
      }
      return result;
    } catch (IOException e) {
      throw new QueryExecutionException("Influx query failed", e);
    } finally {
      synchronized (this) {
        calls.remove(call);
      }
    }
  }

  private Request request(Query query, boolean chunked, QueryTimestampFormat format) {
    var url = endpoint.newBuilder()
        .addQueryParameter("db", query.getDatabase() == null ? database : query.getDatabase())
        .addQueryParameter("q", query.getCommand());
    var epoch = InfluxTimestampEncoding.epoch(format);
    if (epoch != null) {
      url.addQueryParameter("epoch", epoch);
    }
    if (chunked) {
      url.addQueryParameter("chunked", "true").addQueryParameter("chunk_size", "1000");
    }
    if (query.hasBoundParameters()) {
      url.addEncodedQueryParameter("params", query.getParameterJsonWithUrlEncoded());
    }
    var request = new Request.Builder().url(url.build());
    if (query.requiresPost()) {
      request.post(RequestBody.create(null, new byte[0]));
    }
    return request.build();
  }

  private synchronized void release(InfluxQueryCursor cursor) {
    cursors.remove(cursor);
  }

  private void ensureOpen() {
    if (closed) {
      throw new IllegalStateException("Influx query transport is closed");
    }
  }

  @Override
  public synchronized void close() {
    if (!closed) {
      closed = true;
      Set.copyOf(cursors).forEach(InfluxQueryCursor::close);
      calls.forEach(Call::cancel);
      client.dispatcher().executorService().shutdown();
      client.connectionPool().evictAll();
    }
  }
}
