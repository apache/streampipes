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

import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.model.dataset.AggregationFunction;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfluxIdentifierRenderingTest {
  private static final String NAME = "sensor\\\"; value";
  private static final String QUOTED = "\"sensor\\\\\\\"; value\"";

  @Test
  void latestSelectorKeepsFieldAndMeasurementDelimitersLiteral() {
    var query = new InfluxLatestTimestampQuery().compile(Map.of("room/a.b", NAME), "db");
    assertEquals("SELECT LAST(" + QUOTED + ") FROM /^(room\\/a\\.b)$/", query.getCommand());
  }

  @Test
  void fallbackProjectionAndGroupingKeepIdentifiersLiteral() {
    var spec = QuerySpec.builder().select(NAME).groupBy(NAME).build();
    assertEquals("SELECT " + QUOTED + " FROM " + QUOTED + " GROUP BY " + QUOTED + ";",
        new InfluxQueryCompiler("db").compile(spec, NAME).getCommand());
  }

  @Test
  void aggregationAndAliasKeepIdentifiersLiteral() {
    var spec = QuerySpec.builder().aggregate(NAME, AggregationFunction.COUNT, NAME).build();
    assertEquals("SELECT COUNT(" + QUOTED + ") AS " + QUOTED + " FROM \"data\";",
        new InfluxQueryCompiler("db").compile(spec, "data").getCommand());
  }

  @Test
  void preservesSpacesAndUnicodeInFieldNames() {
    var spec = QuerySpec.builder().select(" température ").build();
    assertEquals("SELECT \" température \" FROM \"data\";",
        new InfluxQueryCompiler("db").compile(spec, "data").getCommand());
  }
  @Test
  void quotedFieldCannotEndTheLatestSelector() {
    var field = "t\") FROM data; SELECT LAST(\"t";
    assertEquals("SELECT LAST(\"t\\\") FROM data; SELECT LAST(\\\"t\") FROM /^(data)$/",
        new InfluxLatestTimestampQuery().compile(Map.of("data", field), "db").getCommand());
  }

  @Test
  void comparisonFieldKeepsDelimitersLiteral() {
    var spec = new QuerySpec(java.util.List.of(new QuerySpec.Projection("value",
        java.util.Optional.empty(), java.util.Optional.empty())),
        java.util.List.of(new QuerySpec.Comparison(NAME, QuerySpec.Operator.EQ, new QuerySpec.Literal(1))),
        java.util.Optional.empty(), java.util.List.of(), java.util.Optional.empty(),
        java.util.OptionalInt.empty(), java.util.OptionalInt.empty(), java.util.Optional.empty());
    assertEquals("SELECT value FROM \"data\" WHERE " + QUOTED + " = 1;",
        new InfluxQueryCompiler("db").compile(spec, "data").getCommand());
  }

}
