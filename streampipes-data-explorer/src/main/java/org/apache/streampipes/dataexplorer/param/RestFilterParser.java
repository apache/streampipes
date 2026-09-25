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
import org.apache.streampipes.model.dataset.FilterExpressionCondition;
import org.apache.streampipes.model.dataset.FilterExpressionGroup;
import org.apache.streampipes.model.dataset.FilterExpressionNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/** Parses REST filters without introducing a second query model. */
final class RestFilterParser {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private RestFilterParser() {
  }

  static List<QuerySpec.Predicate> parse(Long start, Long end, String filters, String expression) {
    var group = parseExpression(expression);
    List<QuerySpec.Predicate> conditions = new ArrayList<>();
    // Preserve the historical ordering and parentheses of generated InfluxQL.
    if (end != null) {
      conditions.add(new QuerySpec.TimestampComparison(QuerySpec.Operator.LT, end));
    }
    if (start != null) {
      conditions.add(new QuerySpec.TimestampComparison(QuerySpec.Operator.GT, start));
    }
    if (group == null && filters != null) {
      for (String filter : filters.split(",")) {
        String[] parts = filter.replace("[", "").replace("]", "").split(";");
        if (parts.length != 3) {
          throw new IllegalArgumentException("Invalid filter condition: " + filter);
        }
        conditions.add(comparison(parts[0], parts[1], parts[2]));
      }
    }
    List<QuerySpec.Predicate> predicates = new ArrayList<>();
    if (!conditions.isEmpty()) {
      predicates.add(new QuerySpec.Junction(QuerySpec.BooleanOperator.AND, conditions));
    }
    if (group != null && !group.children().isEmpty()) {
      predicates.add(predicate(group));
    }
    return List.copyOf(predicates);
  }

  private static FilterExpressionGroup parseExpression(String expression) {
    if (expression == null || expression.isBlank()) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(expression, FilterExpressionGroup.class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid filter expression provided", e);
    }
  }

  private static QuerySpec.Predicate predicate(FilterExpressionNode node) {
    if (node instanceof FilterExpressionGroup group) {
      var operator = group.operator() == null ? QuerySpec.BooleanOperator.AND
          : QuerySpec.BooleanOperator.valueOf(group.operator().name());
      return new QuerySpec.Junction(operator, group.children().stream().map(RestFilterParser::predicate).toList());
    }
    if (node instanceof FilterExpressionCondition condition) {
      return comparison(condition.field(), condition.operator(), condition.condition());
    }
    throw new IllegalArgumentException("Unsupported filter expression: " + node);
  }

  private static QuerySpec.Comparison comparison(String field, String operator, Object value) {
    Object scalar = value instanceof String text ? scalar(text) : value;
    return new QuerySpec.Comparison(field, QuerySpec.Operator.fromSymbol(operator), new QuerySpec.Literal(scalar));
  }

  private static Object scalar(String text) {
    if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
      String content = text.substring(1, text.length() - 1);
      if (NumberUtils.isParsable(content)) {
        return content;
      }
    }
    if (NumberUtils.isParsable(text)) {
      return Double.parseDouble(text);
    }
    if ("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text)) {
      return Boolean.parseBoolean(text);
    }
    return text;
  }
}
