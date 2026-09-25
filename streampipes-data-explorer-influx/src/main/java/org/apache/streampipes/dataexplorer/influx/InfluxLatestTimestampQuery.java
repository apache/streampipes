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

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Compiles the batched LAST selector and maps its native metadata response. */
final class InfluxLatestTimestampQuery {
  Query compile(Map<String, String> measurementFields, String database) {
    var query = measurementFields.entrySet()
        .stream()
        .collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new,
            Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
        .entrySet()
        .stream()
        .map(entry -> makeLastSelectorQuery(entry.getKey(), entry.getValue()))
        .collect(Collectors.joining(";"));

    return new Query(query, database);
  }

  private String makeLastSelectorQuery(String field,
                                       List<String> measurements) {
    return "SELECT LAST(\""
        + field
        + "\") FROM /"
        + measurements.stream()
            .map(this::escapeRegex)
            .collect(Collectors.joining("|", "^(", ")$"))
        + "/";
  }

  private String escapeRegex(String measurement) {
    return measurement.replaceAll("([\\\\.\\[\\]{}()*+?^$|])", "\\\\$1");
  }

  Map<String, Long> parse(QueryResult queryResult) {
    Map<String, Long> latestTimestamps = new HashMap<>();
    if (queryResult.getResults() != null) {
      queryResult.getResults().forEach(result -> {
        if (result.getSeries() != null) {
          result.getSeries().forEach(series -> parseLatestTimestampSeries(series, latestTimestamps));
        }
      });
    }
    return latestTimestamps;
  }

  private void parseLatestTimestampSeries(QueryResult.Series series,
                                          Map<String, Long> latestTimestamps) {
    var values = series.getValues();
    if (values != null && !values.isEmpty() && !values.get(0).isEmpty()) {
      latestTimestamps.put(series.getName(), parseTimestamp(values.get(0).get(0)));
    }
  }

  private Long parseTimestamp(Object timestamp) {
    if (timestamp instanceof Number number) {
      return number.longValue();
    } else if (timestamp instanceof String timestampString) {
      try {
        return Long.parseLong(timestampString);
      } catch (NumberFormatException e) {
        return 0L;
      }
    } else {
      return 0L;
    }
  }
}
