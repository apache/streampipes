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

import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.asc;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.desc;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.select;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.api.IDataLakeQueryBuilder;
import org.apache.streampipes.model.datalake.AggregationFunction;
import org.apache.streampipes.model.datalake.DataLakeQueryOrdering;
import org.apache.streampipes.model.datalake.FilterCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.influxdb.dto.Query;
import org.influxdb.querybuilder.Ordering;
import org.influxdb.querybuilder.SelectionQueryImpl;
import org.influxdb.querybuilder.clauses.AndConjunction;
import org.influxdb.querybuilder.clauses.Clause;
import org.influxdb.querybuilder.clauses.ConjunctionClause;
import org.influxdb.querybuilder.clauses.NestedClause;
import org.influxdb.querybuilder.clauses.OrConjunction;
import org.influxdb.querybuilder.clauses.RawTextClause;
import org.influxdb.querybuilder.clauses.SimpleClause;

public class DataLakeInfluxQueryBuilder implements IDataLakeQueryBuilder<Query> {

  private final String measurementId;
  private final SelectionQueryImpl selectionQuery;
  private final List<Clause> whereClauses;
  private final List<Clause> groupByClauses;
  private Ordering ordering;
  private int limit = Integer.MIN_VALUE;
  private int offset = Integer.MIN_VALUE;

  private Object fill;

  private final Environment env;

  private DataLakeInfluxQueryBuilder(String measurementId) {
    this.measurementId = measurementId;
    this.selectionQuery = select();
    this.whereClauses = new ArrayList<>();
    this.groupByClauses = new ArrayList<>();
    this.env = Environments.getEnvironment();
  }

  public static DataLakeInfluxQueryBuilder create(String measurementId) {
    return new DataLakeInfluxQueryBuilder(measurementId);
  }

  @Override
  public DataLakeInfluxQueryBuilder withAllColumns() {
    this.selectionQuery.all();
    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withSimpleColumn(String columnName) {
    this.selectionQuery.column(columnName);
    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withSimpleColumns(List<String> columnNames) {
    columnNames.forEach(this.selectionQuery::column);

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withAggregatedColumn(String columnName, AggregationFunction aggregationFunction,
          String aliasName) {

    this.selectionQuery.function(aggregationFunction.toDbName(), columnName).as(aliasName);

    return this;
  }

  @Override
  public IDataLakeQueryBuilder<Query> withAggregatedColumn(String columnName, AggregationFunction aggregationFunction) {
    this.selectionQuery.function(aggregationFunction.toDbName(), columnName);

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withStartTime(long startTime) {
    this.whereClauses.add(new SimpleClause("time", ">=", startTime * 1000000));
    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withEndTime(long endTime) {
    return withEndTime(endTime, true);
  }

  @Override
  public DataLakeInfluxQueryBuilder withEndTime(long endTime, boolean includeEndTime) {
    String operator = includeEndTime ? "<=" : "<";
    this.whereClauses.add(new SimpleClause("time", operator, endTime * 1000000));
    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withTimeBoundary(long startTime, long endTime) {
    this.withStartTime(startTime);
    this.withEndTime(endTime);

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withFilter(String field, String operator, Object value) {
    this.whereClauses.add(new SimpleClause(field, operator, value));
    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withExclusiveFilter(String field, String operator, List<?> values) {
    List<ConjunctionClause> or = new ArrayList<>();
    values.forEach(value -> or.add(new OrConjunction(new SimpleClause(field, operator, value))));

    addNestedWhereClause(or);
    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withInclusiveFilter(String field, String operator, List<?> values) {
    List<ConjunctionClause> and = new ArrayList<>();
    values.forEach(value -> and.add(new AndConjunction(new SimpleClause(field, operator, value))));

    addNestedWhereClause(and);
    return this;
  }

  @Override
  public IDataLakeQueryBuilder<Query> withInclusiveFilter(List<FilterCondition> filterConditions) {
    List<ConjunctionClause> and = new ArrayList<>();
    filterConditions
            .forEach(c -> and.add(new AndConjunction(new SimpleClause(c.field(), c.operator(), c.condition()))));

    addNestedWhereClause(and);

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withGroupByTime(String timeInterval) {

    this.groupByClauses.add(new RawTextClause("time(" + timeInterval + ")"));

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withGroupByTime(String timeInterval, String offsetInterval) {

    this.groupByClauses.add(new RawTextClause("time(" + timeInterval + "," + offsetInterval + ")"));

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withGroupBy(String column) {

    this.groupByClauses.add(new RawTextClause(column));

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withOrderBy(DataLakeQueryOrdering ordering) {
    if (DataLakeQueryOrdering.ASC.equals(ordering)) {
      this.ordering = asc();
    } else {
      this.ordering = desc();
    }

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withLimit(int limit) {
    this.limit = limit;

    return this;
  }

  @Override
  public DataLakeInfluxQueryBuilder withOffset(int offset) {
    this.offset = offset;

    return this;
  }

  @Override
  public IDataLakeQueryBuilder<Query> withFill(Object fill) {
    this.fill = fill;

    return this;
  }

  @Override
  public Query build() {
    var selectQuery = this.selectionQuery.from(env.getTsStorageBucket().getValueOrDefault(),
            escapeIndex(measurementId));
    this.whereClauses.forEach(selectQuery::where);

    if (!this.groupByClauses.isEmpty()) {
      selectQuery.groupBy(this.groupByClauses.toArray());
    }

    if (this.ordering != null) {
      selectQuery.orderBy(this.ordering);
    }

    if (this.limit != Integer.MIN_VALUE) {
      selectQuery.limit(this.limit);
    }

    if (this.offset > 0) {
      selectQuery.limit(this.limit, this.offset);
    }

    if (Objects.nonNull(fill)) {
      if (fill instanceof String) {
        selectQuery.fill((String) fill);
      } else {
        selectQuery.fill((Number) fill);
      }
    }

    return selectQuery;
  }

  private void addNestedWhereClause(List<ConjunctionClause> clauses) {
    NestedClause nestedClause = new NestedClause(clauses);
    this.whereClauses.add(nestedClause);
  }

  private String escapeIndex(String index) {
    return "\"" + index + "\"";
  }
}
