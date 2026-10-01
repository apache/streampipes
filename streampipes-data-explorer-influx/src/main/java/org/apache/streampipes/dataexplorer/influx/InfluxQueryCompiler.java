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

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCompiler;
import org.apache.streampipes.dataexplorer.api.query.QueryCapabilities;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;

import org.influxdb.dto.Query;
import org.influxdb.querybuilder.clauses.AndConjunction;
import org.influxdb.querybuilder.clauses.Clause;
import org.influxdb.querybuilder.clauses.ConjunctionClause;
import org.influxdb.querybuilder.clauses.NestedClause;
import org.influxdb.querybuilder.clauses.OrConjunction;
import org.influxdb.querybuilder.clauses.RawTextClause;
import org.influxdb.querybuilder.clauses.SimpleClause;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.asc;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.desc;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.select;

/** Stateless InfluxQL compiler. All connection settings are supplied by its owner. */
public final class InfluxQueryCompiler implements DatasetQueryCompiler<Query> {
  private final String database;

  public InfluxQueryCompiler(String database) {
    this.database = Objects.requireNonNull(database);
  }

  @Override
  public QueryCapabilities capabilities() {
    return new QueryCapabilities(EnumSet.allOf(AggregationFunction.class), EnumSet.allOf(QuerySpec.Operator.class),
        true, true, EnumSet.allOf(QuerySpec.FillMode.class), true, false);
  }

  @Override
  public Query compile(QuerySpec specification, String storageName) {
    capabilities().validate(specification);
    var selection = select();
    for (var projection : specification.projections()) {
      if (projection.aggregation().isPresent()) {
        selection.function(projection.aggregation().get().toDbName(), identifier(projection.field()));
      } else if ("*".equals(projection.field())) {
        selection.all();
      } else {
        selection.column(projection.field());
      }
      projection.alias().ifPresent(alias -> selection.as(identifier(alias)));
    }
    var query = selection.from(database, "\"" + storageName + "\"");
    specification.predicates().forEach(predicate -> query.where(clause(predicate)));
    List<Clause> grouping = new ArrayList<>();
    specification.timeBucket().ifPresent(bucket -> grouping.add(new RawTextClause("time("
        + bucket.interval().value() + bucket.offset().map(offset -> "," + offset.toMillis() + "ms").orElse("") + ")")));
    specification.dimensions().forEach(field -> grouping.add(new RawTextClause("\"" + field + "\"")));
    if (!grouping.isEmpty()) {
      query.groupBy(grouping.toArray());
    }
    specification.ordering().ifPresent(order -> query.orderBy(order == DataLakeQueryOrdering.ASC ? asc() : desc()));
    specification.limit().ifPresent(query::limit);
    if (specification.offset().orElse(0) > 0) {
      query.limit(specification.limit().orElse(Integer.MIN_VALUE), specification.offset().getAsInt());
    }
    specification.fill().ifPresent(fill -> {
      if (fill.mode() == QuerySpec.FillMode.CONSTANT) {
        query.fill((Number) fill.constant().orElseThrow().value());
      } else {
        query.fill(fill.mode().name().toLowerCase(Locale.ROOT));
      }
    });
    return query;
  }

  private Clause clause(QuerySpec.Predicate predicate) {
    return switch (predicate) {
      case QuerySpec.Comparison comparison ->
          new SimpleClause(comparison.field(), comparison.operator().symbol(), comparison.value().value());
      case QuerySpec.TimestampComparison timestamp ->
          new SimpleClause("time", timestamp.operator().symbol(), Math.multiplyExact(timestamp.epochMillis(), 1000000));
      case QuerySpec.Junction junction -> {
        List<ConjunctionClause> children = new ArrayList<>();
        for (var child : junction.children()) {
          children.add(junction.operator() == QuerySpec.BooleanOperator.AND
              ? new AndConjunction(clause(child)) : new OrConjunction(clause(child)));
        }
        yield new NestedClause(children);
      }
    };
  }

  private String identifier(String identifier) {
    return identifier.matches("[A-Za-z0-9_]+")
        ? identifier : "\"" + identifier.replace("\"", "\\\"") + "\"";
  }
}
