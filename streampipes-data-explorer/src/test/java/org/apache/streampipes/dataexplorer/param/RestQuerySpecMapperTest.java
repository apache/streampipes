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
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_AGGREGATION_FUNCTION;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COLUMNS;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COUNT_ONLY;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILL;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER_EXPRESSION;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_GROUP_BY;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_OFFSET;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_PAGE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_START_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_TIME_INTERVAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestQuerySpecMapperTest {
  @Test
  void filterScalarTypesArePreserved() {
    var inputs = List.of("6", "true", "a", "3312476503F", "\"6\"");
    var expected = List.of(6.0, true, "a", "3312476503F", "6");
    for (int i = 0; i < inputs.size(); i++) {
      var spec = parse(Map.of(QP_FILTER, "[fieldName;=;" + inputs.get(i) + "]"));
      var group = (QuerySpec.Junction) spec.predicates().getFirst();
      assertEquals(new QuerySpec.Comparison("fieldName", QuerySpec.Operator.EQ,
          new QuerySpec.Literal(expected.get(i))), group.children().getFirst());
    }
  }

  @Test
  void structuredFiltersOverrideLegacyFiltersAndPreserveNumericTypes() {
    var spec = parse(Map.of(QP_FILTER, "invalid legacy filter", QP_START_DATE, "10", QP_END_DATE, "20",
        QP_FILTER_EXPRESSION, """
        {"type":"group","operator":"OR","children":[
          {"type":"condition","field":"value","operator":"=","condition":2},
          {"type":"condition","field":"value","operator":"=","condition":"2"}
        ]}
        """));
    var bounds = (QuerySpec.Junction) spec.predicates().getFirst();
    assertEquals(List.of(new QuerySpec.TimestampComparison(QuerySpec.Operator.LT, 20),
        new QuerySpec.TimestampComparison(QuerySpec.Operator.GT, 10)), bounds.children());
    var filters = (QuerySpec.Junction) spec.predicates().get(1);
    assertEquals(QuerySpec.BooleanOperator.OR, filters.operator());
    assertEquals(2, ((QuerySpec.Comparison) filters.children().getFirst()).value().value());
    assertEquals(2.0, ((QuerySpec.Comparison) filters.children().get(1)).value().value());
  }

  @Test
  void countAndGlobalAggregationOverrideColumnAggregationAndAlias() {
    var params = new HashMap<>(Map.of(QP_COLUMNS, "[value;MIN;low]", QP_AGGREGATION_FUNCTION, "MAX"));
    var projection = parse(params).projections().getFirst();
    assertEquals(AggregationFunction.MAX, projection.aggregation().orElseThrow());
    assertEquals("max_value", projection.alias().orElseThrow());
    params.put(QP_COUNT_ONLY, "true");
    projection = parse(params).projections().getFirst();
    assertEquals(AggregationFunction.COUNT, projection.aggregation().orElseThrow());
    assertEquals("count_value", projection.alias().orElseThrow());
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_COUNT_ONLY, "true")));
  }

  @Test
  void paginationUsesExplicitOffsetBeforePageAndRejectsOverflow() {
    assertEquals(50, parse(Map.of(QP_LIMIT, "25", QP_PAGE, "2")).offset().orElseThrow());
    assertEquals(3, parse(Map.of(QP_LIMIT, "25", QP_PAGE, "2", QP_OFFSET, "3")).offset().orElseThrow());
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_LIMIT, "100", QP_PAGE, "2147483647")));
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_OFFSET, "-1")));
  }

  @Test
  void fillBelongsToTimeBucketsAndDefaultsToNone() {
    assertTrue(parse(Map.of(QP_FILL, "previous")).fill().isEmpty());
    assertEquals(QuerySpec.FillMode.NONE, parse(Map.of(QP_TIME_INTERVAL, "1h")).fill().orElseThrow().mode());
    var fill = parse(Map.of(QP_TIME_INTERVAL, "1h", QP_FILL, "-2.5")).fill().orElseThrow();
    assertEquals(-2.5, fill.constant().orElseThrow().value());
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_TIME_INTERVAL, "1h", QP_FILL, "unsafe()")));
  }

  @Test
  void parsingDoesNotRetainOrMutateRequestParameters() {
    var params = new HashMap<>(Map.of(QP_COLUMNS, "value", QP_GROUP_BY, "machine"));
    var spec = parse(params);
    params.put(QP_COLUMNS, "other");
    assertEquals("value", spec.projections().getFirst().field());
    assertThrows(UnsupportedOperationException.class, () -> spec.dimensions().add("other"));
    assertEquals("*", parse(Map.of()).projections().getFirst().field());
  }

  @Test
  void invalidFiltersFailAtTheRestBoundary() {
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_FILTER_EXPRESSION, "{")));
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_FILTER, "[field;=]")));
    assertThrows(IllegalArgumentException.class, () -> parse(Map.of(QP_FILTER, "[field;invalid;1]")));
  }

  private QuerySpec parse(Map<String, String> params) {
    return RestQuerySpecMapper.parse(new ProvidedRestQueryParams("physical", params));
  }
}
