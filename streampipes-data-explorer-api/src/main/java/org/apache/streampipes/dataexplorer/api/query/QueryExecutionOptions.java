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

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/** Application policies, separate from the selection and its per-series database limit. */
public record QueryExecutionOptions(boolean ignoreMissingValues, OptionalInt maximumRows, boolean autoAggregate,
                                    Optional<QuerySpec.Fill> autoAggregationFill, QueryTimestampFormat timestampFormat) {
  public QueryExecutionOptions {
    Objects.requireNonNull(timestampFormat);
    Objects.requireNonNull(maximumRows);
    Objects.requireNonNull(autoAggregationFill);
    if (maximumRows.isPresent() && maximumRows.getAsInt() < 0) {
      throw new IllegalArgumentException("Maximum rows must not be negative");
    }
  }

  public QueryExecutionOptions(boolean ignoreMissingValues, OptionalInt maximumRows, boolean autoAggregate,
                               Optional<QuerySpec.Fill> autoAggregationFill) {
    this(ignoreMissingValues, maximumRows, autoAggregate, autoAggregationFill, QueryTimestampFormat.EPOCH_MILLIS);
  }

  public QueryExecutionOptions withIgnoreMissingValues(boolean ignoreMissing) {
    return new QueryExecutionOptions(ignoreMissing, maximumRows, autoAggregate, autoAggregationFill, timestampFormat);
  }

  public QueryExecutionOptions withTimestampFormat(QueryTimestampFormat format) {
    return new QueryExecutionOptions(ignoreMissingValues, maximumRows, autoAggregate, autoAggregationFill, format);
  }

  public QueryExecutionOptions(boolean ignoreMissingValues, OptionalInt maximumRows, boolean autoAggregate) {
    this(ignoreMissingValues, maximumRows, autoAggregate, Optional.empty());
  }

  public static QueryExecutionOptions defaults() {
    return new QueryExecutionOptions(false, OptionalInt.empty(), false);
  }
}
