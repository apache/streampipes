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

package org.apache.streampipes.extensions.connectors.filewatcher.adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class WinCCTimestampConverter {

  private static final BigDecimal EXCEL_UNIX_EPOCH_OFFSET_DAYS = BigDecimal.valueOf(25569L);
  private static final BigDecimal MILLIS_PER_DAY = BigDecimal.valueOf(86_400_000L);
  private static final BigDecimal SCALED_DAY_DIVISOR = BigDecimal.valueOf(1_000_000L);
  private static final List<DateTimeFormatter> TIME_STRING_FORMATS = List.of(
      DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"),
      DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
  );

  private WinCCTimestampConverter() {
  }

  public static Long toUnixTimestampMillis(Object winccTimestamp,
                                           Object timeString) {
    return toUnixTimestampMillis(winccTimestamp, timeString, ZoneId.systemDefault());
  }

  public static Long toUnixTimestampMillis(Object winccTimestamp,
                                           Object timeString,
                                           ZoneId zoneId) {
    BigDecimal serialDays = normalizeSerialDays(winccTimestamp);
    if (serialDays != null) {
      return serialDays
          .subtract(EXCEL_UNIX_EPOCH_OFFSET_DAYS)
          .multiply(MILLIS_PER_DAY)
          .setScale(0, RoundingMode.HALF_UP)
          .longValue();
    }

    return parseTimeString(timeString, zoneId);
  }

  private static BigDecimal normalizeSerialDays(Object winccTimestamp) {
    BigDecimal rawValue = toBigDecimal(winccTimestamp);
    if (rawValue == null) {
      return null;
    }

    if (isScaledDayValue(rawValue, winccTimestamp)) {
      return rawValue.divide(SCALED_DAY_DIVISOR, 10, RoundingMode.HALF_UP);
    }

    return rawValue;
  }

  private static boolean isScaledDayValue(BigDecimal rawValue,
                                          Object originalValue) {
    if (originalValue instanceof String stringValue) {
      if (stringValue.contains(",") || stringValue.contains(".")) {
        return false;
      }
    }

    return rawValue.compareTo(BigDecimal.valueOf(1_000_000L)) > 0;
  }

  private static BigDecimal toBigDecimal(Object winccTimestamp) {
    if (winccTimestamp instanceof Number number) {
      return BigDecimal.valueOf(number.doubleValue());
    }

    if (winccTimestamp instanceof String stringValue) {
      String normalizedValue = stringValue.trim().replace(',', '.');
      if (normalizedValue.isEmpty()) {
        return null;
      }

      try {
        return new BigDecimal(normalizedValue);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }

  private static Long parseTimeString(Object timeString,
                                      ZoneId zoneId) {
    if (!(timeString instanceof String stringValue)) {
      return null;
    }

    String trimmedValue = stringValue.trim();
    if (trimmedValue.isEmpty()) {
      return null;
    }

    for (DateTimeFormatter formatter : TIME_STRING_FORMATS) {
      try {
        LocalDateTime dateTime = LocalDateTime.parse(trimmedValue, formatter);
        return dateTime.atZone(zoneId).toInstant().toEpochMilli();
      } catch (DateTimeParseException ignored) {
      }
    }

    return null;
  }
}
