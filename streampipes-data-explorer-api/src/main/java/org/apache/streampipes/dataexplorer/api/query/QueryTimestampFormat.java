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

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** Timestamp representation at a query boundary, independent of database syntax. */
public enum QueryTimestampFormat {
  RFC3339(null),
  EPOCH_NANOS(TimeUnit.NANOSECONDS),
  EPOCH_MICROS(TimeUnit.MICROSECONDS),
  EPOCH_MILLIS(TimeUnit.MILLISECONDS),
  EPOCH_SECONDS(TimeUnit.SECONDS),
  EPOCH_MINUTES(TimeUnit.MINUTES),
  EPOCH_HOURS(TimeUnit.HOURS),
  EPOCH_DAYS(TimeUnit.DAYS);

  private final TimeUnit unit;

  QueryTimestampFormat(TimeUnit unit) {
    this.unit = unit;
  }

  public static QueryTimestampFormat epoch(TimeUnit unit) {
    Objects.requireNonNull(unit);
    return switch (unit) {
      case NANOSECONDS -> EPOCH_NANOS;
      case MICROSECONDS -> EPOCH_MICROS;
      case MILLISECONDS -> EPOCH_MILLIS;
      case SECONDS -> EPOCH_SECONDS;
      case MINUTES -> EPOCH_MINUTES;
      case HOURS -> EPOCH_HOURS;
      case DAYS -> EPOCH_DAYS;
    };
  }

  public long toEpochMillis(Object value) {
    return this == RFC3339 ? Instant.parse((String) value).toEpochMilli()
        : TimeUnit.MILLISECONDS.convert(((Number) value).longValue(), unit);
  }

  /** Matching formats preserve the original value, including native RFC3339 spelling and precision. */
  public Object convert(Object value, QueryTimestampFormat source) {
    Objects.requireNonNull(source);
    if (value == null || source == this) {
      return value;
    }
    if (this != RFC3339 && source != RFC3339) {
      return unit.convert(((Number) value).longValue(), source.unit);
    }
    if (this == RFC3339) {
      long number = ((Number) value).longValue();
      var instant = switch (source) {
        case EPOCH_NANOS -> Instant.ofEpochSecond(0, number);
        case EPOCH_MICROS -> Instant.ofEpochSecond(number / 1_000_000, number % 1_000_000 * 1000);
        case EPOCH_MILLIS -> Instant.ofEpochMilli(number);
        case EPOCH_SECONDS -> Instant.ofEpochSecond(number);
        case EPOCH_MINUTES -> Instant.ofEpochSecond(Math.multiplyExact(number, 60));
        case EPOCH_HOURS -> Instant.ofEpochSecond(Math.multiplyExact(number, 3600));
        case EPOCH_DAYS -> Instant.ofEpochSecond(Math.multiplyExact(number, 86400));
        case RFC3339 -> throw new IllegalStateException("Matching formats require no conversion");
      };
      return instant.toString();
    }
    var instant = Instant.parse((String) value);
    // Duration conversion preserves sub-millisecond precision and truncates coarse units toward zero.
    return unit.convert(java.time.Duration.between(Instant.EPOCH, instant));
  }
}
