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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class QueryTimestampFormatTest {
  @Test
  void convertsAllEpochUnitsAndDerivesMetadataAtOutputPrecision() {
    for (var unit : TimeUnit.values()) {
      var format = QueryTimestampFormat.epoch(unit);
      long output = unit.convert(123456789L, TimeUnit.MILLISECONDS);
      assertEquals(output, format.convert(123456789L, QueryTimestampFormat.EPOCH_MILLIS));
      assertEquals(TimeUnit.MILLISECONDS.convert(output, unit), format.toEpochMillis(output));
    }
  }

  @Test
  void retainsNativeSpellingAndNanosecondPrecision() {
    String iso = "2026-09-09T00:00:00.123456789Z";
    assertSame(iso, QueryTimestampFormat.RFC3339.convert(iso, QueryTimestampFormat.RFC3339));
    long nanos = 1788912000123456789L;
    assertEquals(nanos, QueryTimestampFormat.EPOCH_NANOS.convert(iso, QueryTimestampFormat.RFC3339));
    assertEquals(iso, QueryTimestampFormat.RFC3339.convert(nanos, QueryTimestampFormat.EPOCH_NANOS));
    assertEquals(1788912000123L, QueryTimestampFormat.RFC3339.toEpochMillis(iso));
    assertEquals(1788912000123456L,
        QueryTimestampFormat.EPOCH_MICROS.convert(iso, QueryTimestampFormat.RFC3339));
  }

  @Test
  void handlesNegativeEpochsAndNanosecondBoundaries() {
    assertEquals("1969-12-31T23:59:59.999999999Z",
        QueryTimestampFormat.RFC3339.convert(-1L, QueryTimestampFormat.EPOCH_NANOS));
    assertEquals(-1L, QueryTimestampFormat.EPOCH_NANOS.convert(
        "1969-12-31T23:59:59.999999999Z", QueryTimestampFormat.RFC3339));
    assertEquals(0L, QueryTimestampFormat.EPOCH_SECONDS.convert(
        "1969-12-31T23:59:59.900Z", QueryTimestampFormat.RFC3339));
    for (long boundary : new long[]{Long.MIN_VALUE, Long.MAX_VALUE}) {
      var iso = QueryTimestampFormat.RFC3339.convert(boundary, QueryTimestampFormat.EPOCH_NANOS);
      assertEquals(boundary, QueryTimestampFormat.EPOCH_NANOS.convert(iso, QueryTimestampFormat.RFC3339));
    }
  }
}
