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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.model.dataset.DataSeries;
import org.apache.streampipes.model.dataset.SpQueryResult;
import org.apache.streampipes.model.dataset.SpQueryStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Collects declared-format batches into the chart-facing result contract. The caller owns and closes the cursor. */
public final class QueryResultCollector {
  private QueryResultCollector() {
  }

  public static SpQueryResult collect(DatasetQueryCursor cursor, QueryExecutionOptions options) {
    var sourceFormat = cursor.timestampFormat();
    var outputFormat = options.timestampFormat();
    var result = new SpQueryResult();
    result.setHeaders(new ArrayList<>());
    Map<SeriesKey, DataSeries> series = new LinkedHashMap<>();
    Map<SeriesKey, Object> lastTimestamps = new LinkedHashMap<>();
    while (cursor.hasNext()) {
      var batch = cursor.next();
      if (result.getHeaders().isEmpty()) {
        result.setHeaders(new ArrayList<>(batch.columns()));
      }
      var key = new SeriesKey(batch.columns(), batch.tags());
      int timestamp = batch.columns().indexOf("time");
      var target = series.get(key);
      Object lastTimestamp = null;
      boolean hasTimestamp = false;
      boolean mapTimestamp = timestamp >= 0 && sourceFormat != outputFormat;
      for (var row : batch.rows()) {
        if (timestamp >= 0 && row.get(timestamp) != null) {
          lastTimestamp = row.get(timestamp);
          hasTimestamp = true;
        }
        if (options.ignoreMissingValues() && row.contains(null)) {
          continue;
        }
        if (options.maximumRows().isPresent() && result.getTotal() >= options.maximumRows().getAsInt()) {
          var exceeded = new SpQueryResult();
          exceeded.setSpQueryStatus(SpQueryStatus.TOO_MUCH_DATA);
          exceeded.setTotal(Math.addExact(result.getTotal(), 1));
          return exceeded;
        }
        // The chart-facing contract distinguishes ungrouped (null) from grouped series.
        if (target == null) {
          target = new DataSeries(0, new ArrayList<>(), new ArrayList<>(batch.columns()),
              batch.tags().isEmpty() ? null : batch.tags());
          series.put(key, target);
        }
        var outputRow = new ArrayList<>(row);
        if (mapTimestamp) {
          outputRow.set(timestamp, outputFormat.convert(row.get(timestamp), sourceFormat));
        }
        target.getRows().add(outputRow);
        target.setTotal(target.getTotal() + 1);
        result.setTotal(Math.addExact(result.getTotal(), 1));
      }
      if (hasTimestamp) {
        lastTimestamps.put(key, lastTimestamp);
      }
    }
    result.setLastTimestamp(lastTimestamps.values().stream()
        .map(value -> outputFormat.convert(value, sourceFormat))
        .mapToLong(outputFormat::toEpochMillis).max().orElse(0L));
    result.setAllDataSeries(new ArrayList<>(series.values()));
    return result;
  }

  private record SeriesKey(List<String> columns, Map<String, String> tags) {
  }
}
