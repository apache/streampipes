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

package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCompiler;
import org.apache.streampipes.dataexplorer.api.query.QueryCapabilities;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.api.query.UnsupportedQueryException;
import org.apache.streampipes.model.dataset.AggregationFunction;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/** Compiler for the existing tree-model storage layout (root.streampipes.dataset). */
public final class IotDbQueryCompiler implements DatasetQueryCompiler<String> {

  @Override
  public QueryCapabilities capabilities() {
    return new QueryCapabilities(EnumSet.of(AggregationFunction.MEAN, AggregationFunction.MIN,
        AggregationFunction.MAX, AggregationFunction.COUNT, AggregationFunction.SUM,
        AggregationFunction.FIRST, AggregationFunction.LAST),
        EnumSet.complementOf(EnumSet.of(QuerySpec.Operator.MATCH, QuerySpec.Operator.NOT_MATCH)),
        false, false, Set.of(), false, false);
  }

  @Override
  public String compile(QuerySpec query, String storageName) {
    capabilities().validate(query);
    boolean aggregated = query.projections().stream().anyMatch(p -> p.aggregation().isPresent());
    if (aggregated && query.projections().stream().anyMatch(p -> p.aggregation().isEmpty())) {
      throw new UnsupportedQueryException("IoTDB cannot mix raw and aggregated projections");
    }
    if (aggregated && query.projections().stream().anyMatch(p -> "*".equals(p.field()))) {
      throw new UnsupportedQueryException("IoTDB aggregate projections require an explicit field");
    }
    var sql = new StringBuilder("SELECT ");
    sql.append(query.projections().stream().map(this::projection).collect(Collectors.joining(",")));
    sql.append(" FROM ").append(datasetPath(storageName));
    if (!query.predicates().isEmpty()) {
      sql.append(" WHERE ").append(query.predicates().stream().map(this::predicate)
          .collect(Collectors.joining(" AND ")));
    }
    query.ordering().ifPresent(order -> sql.append(" ORDER BY TIME ").append(order.name()));
    query.limit().ifPresent(limit -> {
      if (limit == 0) {
        throw new UnsupportedQueryException("IoTDB requires a positive limit");
      }
      sql.append(" LIMIT ").append(limit);
    });
    if (query.offset().orElse(0) > 0) {
      sql.append(" OFFSET ").append(query.offset().getAsInt());
    }
    return sql.append(';').toString();
  }

  public static String datasetPath(String storageName) {
    return "root.streampipes." + identifier(storageName);
  }

  static String identifier(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("IoTDB identifier must not be blank");
    }
    return "`" + name.replace("`", "``") + "`";
  }

  private String projection(QuerySpec.Projection projection) {
    var field = "*".equals(projection.field()) ? "*" : identifier(projection.field());
    if (projection.aggregation().isPresent()) {
      field = aggregate(projection.aggregation().get()) + "(" + field + ")";
    }
    return field + projection.alias().map(alias -> " AS " + identifier(alias)).orElse("");
  }

  private String aggregate(AggregationFunction function) {
    return switch (function) {
      case MEAN -> "AVG";
      case MIN -> "MIN_VALUE";
      case MAX -> "MAX_VALUE";
      case COUNT -> "COUNT";
      case SUM -> "SUM";
      case FIRST -> "FIRST_VALUE";
      case LAST -> "LAST_VALUE";
      default -> throw new UnsupportedQueryException("Unsupported IoTDB aggregation: " + function);
    };
  }

  private String predicate(QuerySpec.Predicate predicate) {
    return switch (predicate) {
      case QuerySpec.TimestampComparison time -> "time " + operator(time.operator()) + " " + time.epochMillis();
      case QuerySpec.Comparison comparison -> identifier(comparison.field()) + " "
          + operator(comparison.operator()) + " " + literal(comparison.value());
      case QuerySpec.Junction junction -> {
        if (junction.children().isEmpty()) {
          throw new IllegalArgumentException("An IoTDB predicate group must not be empty");
        }
        yield junction.children().stream().map(this::predicate)
            .collect(Collectors.joining(" " + junction.operator().name() + " ", "(", ")"));
      }
    };
  }

  private String operator(QuerySpec.Operator operator) {
    if (operator == QuerySpec.Operator.MATCH || operator == QuerySpec.Operator.NOT_MATCH) {
      throw new UnsupportedQueryException("Regex filter semantics are not portable to IoTDB");
    }
    return operator.symbol();
  }

  private String literal(QuerySpec.Literal literal) {
    if (literal.value() instanceof String value) {
      return "'" + value.replace("'", "''") + "'";
    }
    return literal.value().toString();
  }
}
