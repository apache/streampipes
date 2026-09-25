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
package org.apache.streampipes.dataexplorer.param;

import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.model.dataset.AggregationFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Parses the column syntax of the data explorer REST API into typed projections. */
public final class RestProjectionParser {

  private RestProjectionParser() {
  }

  public static List<QuerySpec.Projection> parse(String columns, String globalAggregation) {
    if (columns == null) {
      return List.of(new QuerySpec.Projection("*", Optional.empty(), Optional.empty()));
    }
    return Arrays.stream(columns.split(",")).map(column -> parseColumn(column, globalAggregation)).toList();
  }

  public static QuerySpec.Projection parseColumn(String column) {
    return parseColumn(column, null);
  }

  private static QuerySpec.Projection parseColumn(String column, String globalAggregation) {
    String field = column;
    AggregationFunction aggregation = null;
    String alias = null;
    if (column.contains(";")) {
      String[] parts = column.replace("[", "").replace("]", "").split(";");
      if (parts.length < 2) {
        throw new IllegalArgumentException("Wrong query format for query part " + column);
      }
      field = parts[0];
      aggregation = AggregationFunction.valueOf(parts[1]);
      alias = parts.length == 3 ? parts[2] : defaultAlias(aggregation, field);
    }
    if (globalAggregation != null) {
      aggregation = AggregationFunction.valueOf(globalAggregation);
      alias = defaultAlias(aggregation, field);
    }
    return new QuerySpec.Projection(field, Optional.ofNullable(aggregation), Optional.ofNullable(alias));
  }

  private static String defaultAlias(AggregationFunction aggregation, String field) {
    return aggregation.name().toLowerCase(Locale.ROOT) + "_" + field;
  }
}
