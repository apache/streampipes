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

import org.apache.streampipes.extensions.connectors.filewatcher.runtime.EventMapper;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

public class WinCCAlarmEventMapper implements EventMapper {

  private final ZoneId timeZone;

  public WinCCAlarmEventMapper() {
    this(ZoneId.systemDefault());
  }

  public WinCCAlarmEventMapper(ZoneId timeZone) {
    this.timeZone = timeZone;
  }

  @Override
  public Map<String, Object> map(Map<String, Object> rawEvent) {
    Map<String, Object> normalizedEvent = new LinkedHashMap<>();

    normalizedEvent.put("timestamp",
        WinCCTimestampConverter.toUnixTimestampMillis(rawEvent.get("Time_ms"), rawEvent.get("TimeString"), timeZone));
    normalizedEvent.put("timestamp_ms", asString(rawEvent.get("Time_ms")));
    normalizedEvent.put("timestamp_string", asString(rawEvent.get("TimeString")));
    normalizedEvent.put("message_text", asString(rawEvent.get("MsgText")));
    normalizedEvent.put("message_number", toInteger(rawEvent.get("MsgNumber")));
    normalizedEvent.put("alarm_procedure_code", toInteger(rawEvent.get("MsgProc")));
    normalizedEvent.put("alarm_procedure", alarmProcedure(rawEvent.get("MsgProc")));
    normalizedEvent.put("state_after_code", toInteger(rawEvent.get("StateAfter")));
    normalizedEvent.put("state_after", stateAfter(rawEvent.get("StateAfter")));
    normalizedEvent.put("alarm_class_code", toInteger(rawEvent.get("MsgClass")));
    normalizedEvent.put("alarm_class", alarmClass(rawEvent.get("MsgClass")));
    normalizedEvent.put("plc", asString(rawEvent.get("PLC")));

    for (int i = 1; i <= 8; i++) {
      normalizedEvent.put("parameter_" + i, asString(rawEvent.get("Var" + i)));
    }

    // Keep only the original timestamp representations in addition to the normalized fields.
    normalizedEvent.put("raw_Time_ms", asString(rawEvent.get("Time_ms")));
    normalizedEvent.put("raw_TimeString", asString(rawEvent.get("TimeString")));
    return normalizedEvent;
  }

  private String alarmProcedure(Object code) {
    return switch (toInt(code)) {
      case 1 -> "system_event";
      case 2 -> "alarm_bit_operation";
      case 3 -> "alarm_s";
      case 4 -> "diagnostics_event";
      case 7 -> "analog_alarm";
      case 9 -> "program_alarm";
      case 100 -> "alarm_bit_fault";
      default -> "unknown";
    };
  }

  private String stateAfter(Object code) {
    return switch (toInt(code)) {
      case 0 -> "incoming_outgoing";
      case 1 -> "incoming";
      case 2 -> "incoming_acknowledged_outgoing";
      case 3 -> "incoming_acknowledged";
      case 4 -> "pending_after_plc_reset";
      case 6 -> "incoming_outgoing_acknowledged";
      default -> "unknown";
    };
  }

  private String alarmClass(Object code) {
    int numericCode = toInt(code);
    return switch (numericCode) {
      case 0 -> "no_alarm_class";
      case 1 -> "errors";
      case 2 -> "warnings";
      case 3 -> "system";
      case 4 -> "diagnostic_events";
      default -> numericCode >= 64 ? "user_defined" : "unknown";
    };
  }

  private int toInt(Object code) {
    Integer value = toInteger(code);
    if (value != null) {
      return value;
    }

    return -1;
  }

  private Integer toInteger(Object code) {
    if (code instanceof Number number) {
      return number.intValue();
    }

    if (code instanceof String stringValue) {
      String trimmedValue = stringValue.trim();
      if (trimmedValue.isEmpty()) {
        return null;
      }

      try {
        return Integer.parseInt(trimmedValue);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }

  private String asString(Object value) {
    return value == null ? "" : String.valueOf(value);
  }
}
