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

package org.apache.streampipes.connect.transformer.js;

import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraalJsScriptEngineTest {

  private final GraalJsScriptEngine engine = new GraalJsScriptEngine();

  @Test
  void compileAndExecuteDirectFunctionScript() throws ScriptCompilationException, ScriptExecutionException {
    var script = """
        (function(event, out, ctx) {
          out.collect({
            sensor: event.sensor,
            temperatureF: (event.temperatureC * 9 / 5) + 32,
            tags: event.tags,
            active: true
          });
        })
        """;

    var transformer = engine.compile(script);
    var output = new ArrayList<Map<String, Object>>();

    transformer.transform(
        Map.of(
            "sensor", "machine-1",
            "temperatureC", 25,
            "tags", List.of("lab", "qa")
        ),
        output::add,
        null
    );

    assertEquals(1, output.size());
    assertEquals("machine-1", output.get(0).get("sensor"));
    assertEquals(77, output.get(0).get("temperatureF"));
    assertEquals(List.of("lab", "qa"), output.get(0).get("tags"));
    assertEquals(true, output.get(0).get("active"));
  }

  @Test
  void compileAndExecuteNamedTransformFunction() throws ScriptCompilationException, ScriptExecutionException {
    var script = """
        function transform(event, out, ctx) {
          out.collect({
            reading: {
              value: event.value + 1,
              unit: event.unit
            },
            history: [event.value, event.value + 1]
          });
        }
        """;

    var transformer = engine.compile(script);
    var output = new ArrayList<Map<String, Object>>();

    transformer.transform(
        new LinkedHashMap<>(Map.of(
            "value", 41,
            "unit", "kPa"
        )),
        output::add,
        null
    );

    assertEquals(1, output.size());
    assertEquals(
        Map.of(
            "value", 42,
            "unit", "kPa"
        ),
        output.get(0).get("reading")
    );
    assertEquals(List.of(41, 42), output.get(0).get("history"));
  }

  @Test
  void compileFailsWhenNoExecutableTransformExists() {
    var exception = assertThrows(
        ScriptCompilationException.class,
        () -> engine.compile("const value = 42;")
    );

    assertEquals(
        "Graal JS script must evaluate to an executable function or expose a callable 'transform' member",
        exception.getMessage()
    );
  }

  @Test
  void executeWrapsJavaScriptRuntimeErrors() throws ScriptCompilationException {
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          throw new Error("boom");
        }
        """);

    var exception = assertThrows(
        ScriptExecutionException.class,
        () -> transformer.transform(Map.of("value", 1), event -> {
        }, null)
    );

    assertEquals("Graal JS script execution failed", exception.getMessage());
    assertInstanceOf(Exception.class, exception.getCause());
  }
}
