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

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Pattern;

/**
 * Backend-independent selection. Predicates are ANDed in their specified order.
 * Timestamp predicates use epoch milliseconds; providers perform precision conversion.
 * Empty ordering means the provider's ascending time order. Limits retain legacy per-series semantics.
 */
public record QuerySpec(List<Projection> projections,
                        List<Predicate> predicates,
                        Optional<TimeBucket> timeBucket,
                        List<String> dimensions,
                        Optional<DataLakeQueryOrdering> ordering,
                        OptionalInt limit,
                        OptionalInt offset,
                        Optional<Fill> fill,
                        List<String> orderFields) {
  public QuerySpec {
    projections = List.copyOf(projections);
    predicates = List.copyOf(predicates);
    dimensions = List.copyOf(dimensions);
    orderFields = List.copyOf(orderFields);
    Objects.requireNonNull(timeBucket);
    Objects.requireNonNull(ordering);
    Objects.requireNonNull(limit);
    Objects.requireNonNull(offset);
    Objects.requireNonNull(fill);
    if (projections.isEmpty()) {
      throw new IllegalArgumentException("At least one projection is required");
    }
    if (limit.isPresent() && limit.getAsInt() < 0 || offset.isPresent() && offset.getAsInt() < 0) {
      throw new IllegalArgumentException("Limit and offset must not be negative");
    }
  }

  public QuerySpec(List<Projection> projections,
                   List<Predicate> predicates,
                   Optional<TimeBucket> timeBucket,
                   List<String> dimensions,
                   Optional<DataLakeQueryOrdering> ordering,
                   OptionalInt limit,
                   OptionalInt offset,
                   Optional<Fill> fill) {
    this(projections, predicates, timeBucket, dimensions, ordering, limit, offset, fill, List.of());
  }

  public static QuerySpecBuilder builder() {
    return new QuerySpecBuilder();
  }

  /** Fixed buckets aligned relative to the Unix epoch; an absent offset uses the provider default. */
  public record TimeBucket(TimeInterval interval, Optional<Duration> offset) {
    public TimeBucket {
      Objects.requireNonNull(interval);
      Objects.requireNonNull(offset);
      offset.ifPresent(value -> {
        if (value.getNano() % 1_000_000 != 0) {
          throw new IllegalArgumentException("Bucket offsets must have millisecond precision");
        }
        value.toMillis();
      });
    }

    public TimeBucket(TimeInterval interval) {
      this(interval, Optional.empty());
    }
  }

  public record Projection(String field, Optional<AggregationFunction> aggregation, Optional<String> alias) {
    public Projection {
      Objects.requireNonNull(field);
      Objects.requireNonNull(aggregation);
      Objects.requireNonNull(alias);
      if (field.isBlank() || alias.filter(String::isBlank).isPresent()) {
        throw new IllegalArgumentException("Projection fields and aliases must not be blank");
      }
      if ("*".equals(field) && aggregation.isEmpty() && alias.isPresent()) {
        throw new IllegalArgumentException("A wildcard projection cannot have a single alias");
      }
    }
  }

  public sealed interface Predicate permits Comparison, TimestampComparison, Junction {
  }

  public record Comparison(String field, Operator operator, Literal value) implements Predicate {
    public Comparison {
      Objects.requireNonNull(field);
      Objects.requireNonNull(operator);
      Objects.requireNonNull(value);
    }
  }

  public record TimestampComparison(Operator operator, long epochMillis) implements Predicate {
    public TimestampComparison {
      Objects.requireNonNull(operator);
    }
  }

  public record Junction(BooleanOperator operator, List<Predicate> children) implements Predicate {
    public Junction {
      Objects.requireNonNull(operator);
      children = List.copyOf(children);
    }
  }

  public enum BooleanOperator {
    AND, OR
  }

  public enum Operator {
    EQ("="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">="), MATCH("=~"), NOT_MATCH("!~");

    private final String symbol;

    Operator(String symbol) {
      this.symbol = symbol;
    }

    public String symbol() {
      return symbol;
    }

    public static Operator fromSymbol(String symbol) {
      for (var operator : values()) {
        if (operator.symbol.equals(symbol)) {
          return operator;
        }
      }
      throw new IllegalArgumentException("Unsupported comparison operator: " + symbol);
    }
  }

  /** Only immutable scalar values can enter a query. */
  public record Literal(Object value) {
    public Literal {
      if (!(value instanceof String || value instanceof Boolean || value instanceof Byte
          || value instanceof Short || value instanceof Integer || value instanceof Long
          || value instanceof Float || value instanceof Double)) {
        throw new IllegalArgumentException("Expected a string, boolean or numeric query value");
      }
      if (value instanceof Double number && !Double.isFinite(number)
          || value instanceof Float floatValue && !Float.isFinite(floatValue)) {
        throw new IllegalArgumentException("Query numbers must be finite");
      }
    }
  }

  /** Fixed duration notation, retaining the original spelling for legacy query compatibility. */
  public record TimeInterval(String value) {
    private static final Pattern FORMAT = Pattern.compile("^\\d+(ms|s|m|h|d|w)$");

    public TimeInterval {
      if (value == null || !FORMAT.matcher(value).matches()) {
        throw new IllegalArgumentException("Invalid time interval: " + value);
      }
    }

    public Duration duration() {
      int suffix = value.endsWith("ms") ? 2 : 1;
      long amount = Long.parseLong(value.substring(0, value.length() - suffix));
      return switch (value.substring(value.length() - suffix)) {
        case "ms" -> Duration.ofMillis(amount);
        case "s" -> Duration.ofSeconds(amount);
        case "m" -> Duration.ofMinutes(amount);
        case "h" -> Duration.ofHours(amount);
        case "d" -> Duration.ofDays(amount);
        case "w" -> Duration.ofDays(Math.multiplyExact(amount, 7));
        default -> throw new IllegalArgumentException("Invalid time interval: " + value);
      };
    }
  }

  public enum FillMode {
    NONE, NULL, PREVIOUS, LINEAR, CONSTANT
  }

  public record Fill(FillMode mode, Optional<Literal> constant) {
    public Fill {
      Objects.requireNonNull(mode);
      Objects.requireNonNull(constant);
      if ((mode == FillMode.CONSTANT) != constant.isPresent()
          || constant.isPresent() && !(constant.get().value() instanceof Number)) {
        throw new IllegalArgumentException("Constant fill requires a numeric value");
      }
    }

  }
}
