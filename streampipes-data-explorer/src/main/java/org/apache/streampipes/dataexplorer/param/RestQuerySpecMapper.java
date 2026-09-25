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

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_AGGREGATION_FUNCTION;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_AUTO_AGGREGATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COLUMNS;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COUNT_ONLY;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILL;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER_EXPRESSION;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_GROUP_BY;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_OFFSET;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_PAGE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_START_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_TIME_INTERVAL;

/** Transport boundary: maps existing REST syntax directly to the backend-independent query specification. */
public final class RestQuerySpecMapper {

  private RestQuerySpecMapper() {
  }

  public static QuerySpec parse(ProvidedRestQueryParams params) {
    boolean countOnly = params.has(QP_COUNT_ONLY) && params.getAsBoolean(QP_COUNT_ONLY);
    String columns = params.getAsString(QP_COLUMNS);
    if (countOnly && columns == null) {
      throw new IllegalArgumentException("Count queries require columns");
    }
    String aggregation = countOnly ? AggregationFunction.COUNT.name() : params.getAsString(QP_AGGREGATION_FUNCTION);
    var bucket = params.has(QP_TIME_INTERVAL)
        ? Optional.of(new QuerySpec.TimeBucket(new QuerySpec.TimeInterval(params.getAsString(QP_TIME_INTERVAL))))
        : Optional.<QuerySpec.TimeBucket>empty();
    var dimensions = params.has(QP_GROUP_BY)
        ? Arrays.stream(params.getAsString(QP_GROUP_BY).split(","))
            .map(RestQueryParameterValidator::requireSafeIdentifier).toList() : List.<String>of();
    var ordering = "DESC".equals(params.getAsString(QP_ORDER))
        ? Optional.of(DataLakeQueryOrdering.DESC) : Optional.<DataLakeQueryOrdering>empty();
    var limit = integer(params, QP_LIMIT);
    var offset = integer(params, QP_OFFSET);
    if (offset.isEmpty() && limit.isPresent() && params.has(QP_PAGE)) {
      try {
        offset = OptionalInt.of(Math.multiplyExact(params.getAsInt(QP_PAGE), limit.getAsInt()));
      } catch (ArithmeticException e) {
        throw new IllegalArgumentException("Pagination offset is too large", e);
      }
    }
    return new QuerySpec(RestProjectionParser.parse(columns, aggregation),
        RestFilterParser.parse(params.getAsLong(QP_START_DATE), params.getAsLong(QP_END_DATE),
            params.getAsString(QP_FILTER), params.getAsString(QP_FILTER_EXPRESSION)),
        bucket, dimensions, ordering, limit, offset,
        bucket.isPresent() ? Optional.of(RestQueryParameterValidator.parseFill(params.getAsString(QP_FILL)))
            : Optional.empty());
  }

  public static QueryExecutionOptions options(ProvidedRestQueryParams params, boolean ignoreMissingValues) {
    boolean autoAggregate = params.has(QP_AUTO_AGGREGATE) && params.getAsBoolean(QP_AUTO_AGGREGATE);
    var fill = autoAggregate && params.has(QP_FILL)
        ? Optional.of(RestQueryParameterValidator.parseFill(params.getAsString(QP_FILL)))
        : Optional.<QuerySpec.Fill>empty();
    return new QueryExecutionOptions(ignoreMissingValues, integer(params, QP_MAXIMUM_AMOUNT_OF_EVENTS),
        autoAggregate, fill);
  }

  private static OptionalInt integer(ProvidedRestQueryParams params, String name) {
    return params.has(name) ? OptionalInt.of(params.getAsInt(name)) : OptionalInt.empty();
  }
}
