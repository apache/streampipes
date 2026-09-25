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

package org.apache.streampipes.dataexplorer.api.query;

import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** Optional construction helper. Each build returns an independent immutable specification. */
public final class QuerySpecBuilder {
  private final List<QuerySpec.Projection> projections = new ArrayList<>();
  private final List<QuerySpec.Predicate> predicates = new ArrayList<>();
  private final List<String> dimensions = new ArrayList<>();
  private Optional<QuerySpec.TimeBucket> timeBucket = Optional.empty();
  private Optional<DataLakeQueryOrdering> ordering = Optional.empty();
  private List<String> orderFields = List.of();
  private OptionalInt limit = OptionalInt.empty();
  private OptionalInt offset = OptionalInt.empty();
  private Optional<QuerySpec.Fill> fill = Optional.empty();

  public QuerySpecBuilder select(String field) {
    projections.add(new QuerySpec.Projection(field, Optional.empty(), Optional.empty()));
    return this;
  }

  public QuerySpecBuilder select(List<String> fields) {
    fields.forEach(this::select);
    return this;
  }

  public QuerySpecBuilder aggregate(String field, AggregationFunction function) {
    return aggregate(field, function, null);
  }

  public QuerySpecBuilder aggregate(String field, AggregationFunction function, String alias) {
    projections.add(new QuerySpec.Projection(field, Optional.of(function), Optional.ofNullable(alias)));
    return this;
  }

  public QuerySpecBuilder where(QuerySpec.Predicate predicate) {
    predicates.add(predicate);
    return this;
  }

  public QuerySpecBuilder bucket(QuerySpec.TimeBucket bucket) {
    timeBucket = Optional.of(bucket);
    return this;
  }

  public QuerySpecBuilder groupBy(String field) {
    dimensions.add(field);
    return this;
  }

  public QuerySpecBuilder orderBy(DataLakeQueryOrdering direction) {
    return orderBy(direction, List.of());
  }

  public QuerySpecBuilder orderBy(DataLakeQueryOrdering direction, List<String> fields) {
    ordering = Optional.of(direction);
    orderFields = List.copyOf(fields);
    return this;
  }

  public QuerySpecBuilder limit(int value) {
    limit = OptionalInt.of(value);
    return this;
  }

  public QuerySpecBuilder offset(int value) {
    offset = OptionalInt.of(value);
    return this;
  }

  public QuerySpecBuilder fill(QuerySpec.Fill value) {
    fill = Optional.of(value);
    return this;
  }

  public QuerySpec build() {
    return new QuerySpec(projections, predicates, timeBucket, dimensions, ordering, limit, offset, fill, orderFields);
  }
}
