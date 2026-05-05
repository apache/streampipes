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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import java.util.Locale;
import java.util.Map;

public class OpcUaAlarmEventFilter {

  private final OpcUaAlarmAdapterConfig config;

  OpcUaAlarmEventFilter(OpcUaAlarmAdapterConfig config) {
    this.config = config;
  }

  boolean matches(Map<String, Object> event) {
    return matchesSourceName(event)
        && matchesMinimumSeverity(event);
  }

  private boolean matchesSourceName(Map<String, Object> event) {
    if (config.getSourceNameFilter() == null || config.getSourceNameFilter().isBlank()) {
      return true;
    }

    var sourceName = event.get("sourceName");
    if (!(sourceName instanceof String sourceNameValue)) {
      return false;
    }

    return sourceNameValue.toLowerCase(Locale.ENGLISH)
        .contains(config.getSourceNameFilter().toLowerCase(Locale.ENGLISH));
  }

  private boolean matchesMinimumSeverity(Map<String, Object> event) {
    if (config.getMinimumSeverity() == null) {
      return true;
    }

    var severity = event.get("severity");
    if (!(severity instanceof Number severityValue)) {
      return false;
    }

    return severityValue.intValue() >= config.getMinimumSeverity();
  }
}
