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
import org.apache.streampipes.dataexplorer.param.RestQuerySpecMapper;
import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COLUMNS;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_GROUP_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InfluxQueryCompilerTest {
  /** Frozen from the legacy REST converter and builder before their removal. */
  @Test
  void restQueriesMatchLegacyGoldenFixtures() throws Exception {
    try (var input = getClass().getResourceAsStream("/influx-query-baseline.json")) {
      var fixtures = new com.fasterxml.jackson.databind.ObjectMapper().readTree(input);
      assertEquals(64, fixtures.size());
      for (var fixture : fixtures) {
        Map<String, String> values = new HashMap<>();
        fixture.get("params").properties().forEach(entry -> values.put(entry.getKey(), entry.getValue().asText()));
        var spec = RestQuerySpecMapper.parse(new ProvidedRestQueryParams("physical", values));
        var compiler = new InfluxQueryCompiler("database");
        if (fixture.get("command").isNull()) {
          assertThrows(IllegalArgumentException.class, () -> compiler.compile(spec, "physical"), values.toString());
        } else {
          var actual = compiler.compile(spec, "physical");
          assertEquals(fixture.get("command").asText(), actual.getCommand(), values.toString());
          assertEquals("database", actual.getDatabase());
        }
      }
    }
  }

  @Test
  void specificationsAreSnapshotsAndCompilationIsRepeatable() {
    var params = RestQuerySpecMapper.parse(new ProvidedRestQueryParams("physical",
        Map.of(QP_COLUMNS, "value", QP_GROUP_BY, "machine")));
    var spec = params;
    assertThrows(UnsupportedOperationException.class, () -> spec.dimensions().add("other"));
    assertThrows(UnsupportedOperationException.class, () -> spec.projections().clear());
    var compiler = new InfluxQueryCompiler("database");
    assertEquals(compiler.compile(spec, "physical").getCommand(), compiler.compile(spec, "physical").getCommand());
  }

  @Test
  void typedRawProjectionAliasIsNotSilentlyIgnored() {
    var spec = new QuerySpec(
        List.of(new QuerySpec.Projection("value", Optional.empty(), Optional.of("renamed"))),
        List.of(), Optional.empty(), List.of(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(),
        Optional.empty());
    assertEquals("SELECT value AS renamed FROM \"physical\";",
        new InfluxQueryCompiler("database").compile(spec, "physical").getCommand());
  }

  @Test
  void metadataCountQueriesPreserveInclusiveBoundsAndUnaliasedProjection() {
    var spec = new QuerySpec(
        List.of(new QuerySpec.Projection("value", Optional.of(AggregationFunction.COUNT), Optional.empty())),
        List.of(new QuerySpec.TimestampComparison(QuerySpec.Operator.LE, 200),
            new QuerySpec.TimestampComparison(QuerySpec.Operator.GE, 100)),
        Optional.empty(), List.of(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), Optional.empty());
    assertEquals("SELECT COUNT(value) FROM \"physical\" WHERE time <= 200000000 AND time >= 100000000;",
        new InfluxQueryCompiler("database").compile(spec, "physical").getCommand());
  }

  @Test
  void invalidScalarAndOperatorAreRejectedBeforeRendering() {
    assertThrows(IllegalArgumentException.class, () -> new QuerySpec.Literal(new ArrayList<>()));
    assertThrows(IllegalArgumentException.class, () -> new QuerySpec.Literal(Double.NaN));
    assertThrows(IllegalArgumentException.class, () -> QuerySpec.Operator.fromSymbol("= 1; DROP"));
    assertThrows(IllegalArgumentException.class, () -> new QuerySpec.TimeInterval("1h) FROM other"));
  }

  @Test
  void bucketAlignmentIsExplicitAndPreservesMillisecondOffset() {
    var spec = QuerySpec.builder().aggregate("value", AggregationFunction.MEAN, "average")
        .bucket(new QuerySpec.TimeBucket(new QuerySpec.TimeInterval("1d"),
            Optional.of(java.time.Duration.ofMillis(1_700_000_000_123L))))
        .groupBy("machine").build();
    assertEquals("SELECT MEAN(value) AS average FROM \"physical\""
            + " GROUP BY time(1d,1700000000123ms),\"machine\";",
        new InfluxQueryCompiler("database").compile(spec, "physical").getCommand());
  }

  @Test
  void constructionHelperBuildsIndependentSnapshots() {
    var builder = QuerySpec.builder().select("value");
    var first = builder.build();
    builder.select("other").limit(5);
    assertEquals(1, first.projections().size());
    assertEquals(OptionalInt.empty(), first.limit());
    assertEquals(2, builder.build().projections().size());
  }

  @Test
  void providerRejectsFieldOrderingAndSubMillisecondBucketOffsets() {
    var spec = QuerySpec.builder().select("value")
        .orderBy(org.apache.streampipes.model.dataset.DataLakeQueryOrdering.DESC, List.of("value")).build();
    assertThrows(org.apache.streampipes.dataexplorer.api.query.UnsupportedQueryException.class,
        () -> new InfluxQueryCompiler("database").compile(spec, "physical"));
    assertThrows(IllegalArgumentException.class, () -> new QuerySpec.TimeBucket(new QuerySpec.TimeInterval("1h"),
        Optional.of(java.time.Duration.ofNanos(1))));
  }

}
