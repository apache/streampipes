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

package org.apache.streampipes.dataexplorer.sdk;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.v4.params.ColumnFunction;

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

import java.util.ArrayList;
import java.util.List;

import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.asc;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.desc;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.select;

public class DataLakeQueryBuilder implements IDataLakeQueryBuilder<Query> {

  private final String measurementId;
  private final SelectionQueryImpl selectionQuery;
  private final List<Clause> whereClauses;
  private final List<Clause> groupByClauses;
  private Ordering ordering;
  private int limit = Integer.MIN_VALUE;
  private int offset = Integer.MIN_VALUE;

  private Environment env;

  private DataLakeQueryBuilder(String measurementId) {
    this.measurementId = measurementId;
    this.selectionQuery = select();
    this.whereClauses = new ArrayList<>();
    this.groupByClauses = new ArrayList<>();
    this.env = Environments.getEnvironment();
  }

  public static DataLakeQueryBuilder create(String measurementId) {
    return new DataLakeQueryBuilder(measurementId);
  }

  @Override
  public DataLakeQueryBuilder withSimpleColumn(String columnName) {
    this.selectionQuery.column(columnName);

    return this;
  }

  @Override
  public DataLakeQueryBuilder withSimpleColumns(List<String> columnNames) {
    columnNames.forEach(this.selectionQuery::column);

    return this;
  }

  @Override
  public DataLakeQueryBuilder withAggregatedColumn(String columnName,
                                                   ColumnFunction columnFunction,
                                                   String targetName) {
    if (columnFunction == ColumnFunction.COUNT) {
      this.selectionQuery.count(columnName).as(targetName);
    } else if (columnFunction == ColumnFunction.MEAN) {
      this.selectionQuery.mean(columnName).as(targetName);
    } else if (columnFunction == ColumnFunction.MIN) {
      this.selectionQuery.min(columnName).as(targetName);
    } else if (columnFunction == ColumnFunction.MAX) {
      this.selectionQuery.max(columnName).as(targetName);
    } else if (columnFunction == ColumnFunction.FIRST) {
      this.selectionQuery.function("FIRST", columnName).as(targetName);
    } else if (columnFunction == ColumnFunction.LAST) {
      this.selectionQuery.function("LAST", columnName).as(targetName);
    }

    // TODO implement all column functions

    return this;
  }

  @Override
  public DataLakeQueryBuilder withStartTime(long startTime) {
    this.whereClauses.add(new SimpleClause("time", ">=", startTime * 1000000));
    return this;
  }


  @Override
  public DataLakeQueryBuilder withEndTime(long endTime) {
    return withEndTime(endTime, true);
  }

  @Override
  public DataLakeQueryBuilder withEndTime(long endTime,
                                          boolean includeEndTime) {
    String operator = includeEndTime ? "<=" : "<";
    this.whereClauses.add(new SimpleClause("time", operator, endTime * 1000000));
    return this;
  }

  @Override
  public DataLakeQueryBuilder withTimeBoundary(long startTime,
                                               long endTime) {
    this.withStartTime(startTime);
    this.withEndTime(endTime);

    return this;
  }

  @Override
  public DataLakeQueryBuilder withFilter(String field,
                                         String operator,
                                         Object value) {
    this.whereClauses.add(new SimpleClause(field, operator, value));
    return this;
  }

  @Override
  public DataLakeQueryBuilder withExclusiveFilter(String field,
                                                  String operator,
                                                  List<?> values) {
    List<ConjunctionClause> or = new ArrayList<>();
    values.forEach(value -> or.add(new OrConjunction(new SimpleClause(field, operator, value))));

    addNestedWhereClause(or);
    return this;
  }

  @Override
  public DataLakeQueryBuilder withInclusiveFilter(String field,
                                                  String operator,
                                                  List<?> values) {
    List<ConjunctionClause> and = new ArrayList<>();
    values.forEach(value -> and.add(new AndConjunction(new SimpleClause(field, operator, value))));

    addNestedWhereClause(and);
    return this;
  }

  @Override
  public DataLakeQueryBuilder withFilter(NestedClause clause) {
    this.whereClauses.add(clause);

    return this;
  }

  @Override
  public DataLakeQueryBuilder withGroupByTime(String timeInterval) {

    this.groupByClauses.add(new RawTextClause("time(" + timeInterval + ")"));

    return this;
  }

  @Override
  public DataLakeQueryBuilder withGroupByTime(String timeInterval,
                                              String offsetInterval) {

    this.groupByClauses.add(new RawTextClause("time("
        + timeInterval
        + ","
        + offsetInterval
        + ")"));

    return this;
  }

  @Override
  public DataLakeQueryBuilder withGroupBy(String column) {

    this.groupByClauses.add(new RawTextClause(column));

    return this;
  }

  @Override
  public DataLakeQueryBuilder withOrderBy(DataLakeQueryOrdering ordering) {
    if (DataLakeQueryOrdering.ASC.equals(ordering)) {
      this.ordering = asc();
    } else {
      this.ordering = desc();
    }

    return this;
  }

  @Override
  public DataLakeQueryBuilder withLimit(int limit) {
    this.limit = limit;

    return this;
  }

  @Override
  public DataLakeQueryBuilder withOffset(int offset) {
    this.offset = offset;

    return this;
  }

  @Override
  public Query build() {
    var selectQuery =
        this.selectionQuery.from(env.getTsStorageBucket().getValueOrDefault(), "\"" + measurementId + "\"");
    this.whereClauses.forEach(selectQuery::where);

    if (this.groupByClauses.size() > 0) {
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

    return selectQuery;
  }

  private void addNestedWhereClause(List<ConjunctionClause> clauses) {
    NestedClause nestedClause = new NestedClause(clauses);
    this.whereClauses.add(nestedClause);
  }
}
