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

import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class WinCCAlarmEventMapperTest {

  @Test
  void shouldMapRawWinCCAlarmToNormalizedEvent() {
    var rawEvent = Map.<String, Object>ofEntries(
        Map.entry("Time_ms", "46120366239"),
        Map.entry("MsgProc", "1"),
        Map.entry("StateAfter", "3"),
        Map.entry("MsgClass", "2"),
        Map.entry("MsgNumber", "110001"),
        Map.entry("Var1", "A"),
        Map.entry("Var2", "B"),
        Map.entry("Var3", ""),
        Map.entry("Var4", ""),
        Map.entry("Var5", ""),
        Map.entry("Var6", ""),
        Map.entry("Var7", ""),
        Map.entry("Var8", ""),
        Map.entry("TimeString", "08.04.26 08:47"),
        Map.entry("MsgText", "Alarm text"),
        Map.entry("PLC", "PLC_1")
    );

    var mappedEvent = new WinCCAlarmEventMapper(ZoneId.of("Europe/Berlin")).map(rawEvent);

    assertEquals(1775638043050L, mappedEvent.get("timestamp"));
    assertEquals("46120366239", mappedEvent.get("timestamp_ms"));
    assertEquals("08.04.26 08:47", mappedEvent.get("timestamp_string"));
    assertEquals("Alarm text", mappedEvent.get("message_text"));
    assertEquals(110001, mappedEvent.get("message_number"));
    assertEquals(1, mappedEvent.get("alarm_procedure_code"));
    assertEquals("system_event", mappedEvent.get("alarm_procedure"));
    assertEquals(3, mappedEvent.get("state_after_code"));
    assertEquals("incoming_acknowledged", mappedEvent.get("state_after"));
    assertEquals(2, mappedEvent.get("alarm_class_code"));
    assertEquals("warnings", mappedEvent.get("alarm_class"));
    assertEquals("PLC_1", mappedEvent.get("plc"));
    assertEquals("A", mappedEvent.get("parameter_1"));
    assertEquals("B", mappedEvent.get("parameter_2"));
    assertFalse(mappedEvent.containsKey("raw_Var1"));
    assertNull(mappedEvent.get("raw_MsgText"));
    assertNull(mappedEvent.get("raw_StateAfter"));
    assertEquals("46120366239", mappedEvent.get("raw_Time_ms"));
    assertEquals("08.04.26 08:47", mappedEvent.get("raw_TimeString"));
  }
}
