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

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfluxProjectionTest {

  private static final String MEASUREMENT = "measurement";
  @Test
  public void withSimpleColumnsTest() {
    var result = compile(List.of(raw("one"), raw("two")), List.of());

    var expected = String.format("SELECT one,two FROM \"%s\";", MEASUREMENT);
    assertEquals(expected , result.getCommand());
  }

  @Test
  public void withAggregatedColumnEscapesDottedFieldAndAliasTest() {
    var result = compile(List.of(new QuerySpec.Projection("temperature.a",
        Optional.of(AggregationFunction.MEAN), Optional.of("temperature.a"))), List.of());

    var expected = String.format("SELECT MEAN(\"temperature.a\") AS \"temperature.a\" FROM \"%s\";", MEASUREMENT);
    assertEquals(expected, result.getCommand());
  }

  @Test
  public void withGroupByEscapesDottedFieldTest() {
    var result = compile(List.of(raw("value")), List.of("temperature.a"));

    var expected = String.format("SELECT value FROM \"%s\" GROUP BY \"temperature.a\";", MEASUREMENT);
    assertEquals(expected, result.getCommand());
  }
  private QuerySpec.Projection raw(String field) {
    return new QuerySpec.Projection(field, Optional.empty(), Optional.empty());
  }

  private org.influxdb.dto.Query compile(List<QuerySpec.Projection> projections, List<String> dimensions) {
    var spec = new QuerySpec(projections, List.of(), Optional.empty(), dimensions, Optional.empty(),
        OptionalInt.empty(), OptionalInt.empty(), Optional.empty());
    return new InfluxQueryCompiler("database").compile(spec, MEASUREMENT);
  }

}
