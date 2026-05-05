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

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpcUaAlarmEventFilterTest {

  @Test
  void matchesAllEventsWhenNoSpecificFilterIsConfigured() {
    var config = new OpcUaAlarmAdapterConfig();
    var filter = new OpcUaAlarmEventFilter(config);

    assertTrue(filter.matches(Map.of("sourceName", "Pump 1", "severity", 100)));
  }

  @Test
  void filtersBySourceNameAndSeverity() {
    var config = new OpcUaAlarmAdapterConfig();
    config.setSourceNameFilter("pump");
    config.setMinimumSeverity(500);

    var filter = new OpcUaAlarmEventFilter(config);

    assertTrue(filter.matches(Map.of(
        "sourceName", "Pump 1",
        "severity", 700
    )));

    assertFalse(filter.matches(Map.of(
        "sourceName", "Boiler 1",
        "severity", 700
    )));

    assertFalse(filter.matches(Map.of(
        "sourceName", "Pump 1",
        "severity", 300
    )));
  }
}
