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

import java.util.Set;

/** Coarse feature discovery; compilers additionally validate supported feature combinations. */
public record QueryCapabilities(Set<AggregationFunction> aggregations,
                                Set<QuerySpec.Operator> operators,
                                boolean timeBuckets,
                                boolean dimensionGrouping,
                                Set<QuerySpec.FillMode> fillModes,
                                boolean bucketOffsets,
                                boolean fieldOrdering) {
  public QueryCapabilities {
    aggregations = Set.copyOf(aggregations);
    operators = Set.copyOf(operators);
    fillModes = Set.copyOf(fillModes);
  }

  public void validate(QuerySpec query) {
    query.projections().forEach(projection -> projection.aggregation().ifPresent(function -> {
      require(aggregations.contains(function), "aggregation " + function);
    }));
    require(query.timeBucket().isEmpty() || timeBuckets, "time buckets");
    require(query.timeBucket().flatMap(QuerySpec.TimeBucket::offset).isEmpty() || bucketOffsets, "bucket offsets");
    require(query.orderFields().isEmpty() || fieldOrdering, "field ordering");
    require(query.dimensions().isEmpty() || dimensionGrouping, "dimension grouping");
    query.fill().ifPresent(fill -> require(fillModes.contains(fill.mode()), "fill mode " + fill.mode()));
    query.predicates().forEach(this::validatePredicate);
  }

  private void validatePredicate(QuerySpec.Predicate predicate) {
    switch (predicate) {
      case QuerySpec.Comparison comparison -> require(operators.contains(comparison.operator()),
          "operator " + comparison.operator());
      case QuerySpec.TimestampComparison timestamp -> require(operators.contains(timestamp.operator()),
          "timestamp operator " + timestamp.operator());
      case QuerySpec.Junction junction -> junction.children().forEach(this::validatePredicate);
    }
  }

  private void require(boolean supported, String feature) {
    if (!supported) {
      throw new UnsupportedQueryException("Storage provider does not support " + feature);
    }
  }
}
