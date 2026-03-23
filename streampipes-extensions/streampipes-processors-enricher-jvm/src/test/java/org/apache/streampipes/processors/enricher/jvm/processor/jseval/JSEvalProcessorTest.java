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

package org.apache.streampipes.processors.enricher.jvm.processor.jseval;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class JSEvalProcessorTest {

  @Test
  void transformsFieldsWithJavascriptFunction() {
    var configuration = TestConfiguration.builder()
        .config("jsFunction", """
            function process(event) {
              return {
                id: event.id,
                temperatureF: (event.temperatureC * 9 / 5) + 32
              };
            }
            """)
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        event("id", "sensor-1", "temperatureC", 20.0d),
        event("id", "sensor-2", "temperatureC", 25.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("id", "sensor-1", "temperatureF", 68.0d),
        event("id", "sensor-2", "temperatureF", 77.0d)
    );

    new ProcessingElementTestExecutor(new JSEvalProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }

  @Test
  void canReturnSubsetAndDerivedFields() {
    var configuration = TestConfiguration.builder()
        .config("jsFunction", """
            function process(event) {
              return {
                label: event.device + "-" + event.status,
                alert: event.value > 50
              };
            }
            """)
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        event("device", "sensor-1", "status", "ok", "value", 42.0d),
        event("device", "sensor-2", "status", "warn", "value", 65.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("label", "sensor-1-ok", "alert", false),
        event("label", "sensor-2-warn", "alert", true)
    );

    new ProcessingElementTestExecutor(new JSEvalProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }

  private Map<String, Object> event(Object... keyValuePairs) {
    var event = new LinkedHashMap<String, Object>();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      event.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
    }
    return event;
  }
}
