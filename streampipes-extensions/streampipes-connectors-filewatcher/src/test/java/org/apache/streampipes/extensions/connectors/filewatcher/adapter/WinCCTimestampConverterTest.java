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

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WinCCTimestampConverterTest {

  @Test
  void shouldConvertWinCCTimestampToUnixMillis() {
    assertEquals(1072876370999L,
        WinCCTimestampConverter.toUnixTimestampMillis("37986.55059027", "30.12.03 13:12:51", ZoneId.of("UTC")));
    assertEquals(1072876370999L,
        WinCCTimestampConverter.toUnixTimestampMillis("37986,55059027", "30.12.03 13:12:51", ZoneId.of("UTC")));
    assertEquals(1775638043050L,
        WinCCTimestampConverter.toUnixTimestampMillis(46120366239L, "08.04.26 08:47", ZoneId.of("UTC")));
  }

  @Test
  void shouldReturnNullForInvalidInput() {
    assertNull(WinCCTimestampConverter.toUnixTimestampMillis(null, null, ZoneId.of("UTC")));
    assertNull(WinCCTimestampConverter.toUnixTimestampMillis("", "", ZoneId.of("UTC")));
    assertNull(WinCCTimestampConverter.toUnixTimestampMillis("not-a-timestamp", "also-invalid", ZoneId.of("UTC")));
  }

  @Test
  void shouldFallbackToTimeStringWhenNeeded() {
    long expectedTimestamp = LocalDateTime.parse("08.04.26 08:47", DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"))
        .atZone(ZoneId.of("Europe/Berlin"))
        .toInstant()
        .toEpochMilli();

    assertEquals(expectedTimestamp,
        WinCCTimestampConverter.toUnixTimestampMillis(null, "08.04.26 08:47", ZoneId.of("Europe/Berlin")));
  }
}
