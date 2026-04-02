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

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.api.credentials.CredentialsProvider;
import org.apache.streampipes.connect.transformer.api.Context;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;

import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Map.of(
            "value", 41,
            "unit", "kPa"
        ),
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

  @Test
  void executeProvidesUtilityFunctions() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          utils.rename(event, "temperature", "temp");
          utils.remove(event, "obsolete");
          utils.parseTimestamp(event, "createdAt");
          out.collect(utils.addTimestamp(event, "processedAt"));
        }
        """);

    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(
        new LinkedHashMap<>(Map.of(
            "temperature", 23,
            "obsolete", "remove-me",
            "createdAt", "2024-01-02T03:04:05Z"
        )),
        output::add,
        null
    );

    assertEquals(1, output.size());
    assertEquals(23, output.get(0).get("temp"));
    assertEquals(1704164645000L, output.get(0).get("timestamp"));
    assertTrue(output.get(0).containsKey("processedAt"));
    assertFalse(output.get(0).containsKey("temperature"));
    assertFalse(output.get(0).containsKey("obsolete"));
  }

  @Test
  void executeFailsWhenUtilityTimestampParsingFails() throws ScriptCompilationException {
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          utils.parseTimestamp(event, "createdAt");
          out.collect(event);
        }
        """);

    var exception = assertThrows(
        ScriptExecutionException.class,
        () -> transformer.transform(Map.of("createdAt", "not-a-date"), event -> {
        }, null)
    );

    assertEquals("Graal JS script execution failed", exception.getMessage());
    assertInstanceOf(Exception.class, exception.getCause());
  }

  @Test
  void executeClientThroughContext() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          out.collect({
            hasClient: ctx.client() !== null
          });
        }
        """);

    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(
        Map.of("value", 1),
        output::add,
        new StreamPipesScriptContext(offlineClient())
    );

    assertEquals(1, output.size());
    assertEquals(true, output.get(0).get("hasClient"));
  }

  @Test
  void executeCanReadClientResultsInsideVm() throws Exception {
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          const adapters = ctx.client().adapters().all();
          out.collect({
            adapterId: adapters[0].elementId,
            adapterName: adapters[0].name
          });
        }
        """);

    var output = executeSingleEvent(
        transformer,
        Map.of("input", "value"),
        scriptContext(scriptClientWithAdapters("adapter-js", "JavaScript Adapter"))
    );

    assertEquals("adapter-js", output.get("adapterId"));
    assertEquals("JavaScript Adapter", output.get("adapterName"));
  }

  @Test
  void executeCanSendGuestObjectToClient() throws Exception {
    var createdAdapters = new ArrayList<Map<String, Object>>();
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          ctx.client().adapters().create({
            elementId: "created-js",
            name: "Created From JS",
            running: false
          });
          out.collect({created: "created-js"});
        }
        """);

    var output = executeSingleEvent(
        transformer,
        Map.of("input", "value"),
        scriptContext(scriptClientWithCreateRecorder(createdAdapters))
    );

    assertEquals("created-js", output.get("created"));
    assertEquals(1, createdAdapters.size());
    assertEquals("created-js", createdAdapters.get(0).get("elementId"));
    assertEquals("Created From JS", createdAdapters.get(0).get("name"));
    assertEquals(false, createdAdapters.get(0).get("running"));
  }

  @Test
  void executeExposesProxyClientMethodsThroughContext() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        function transform(event, out, ctx) {
          out.collect({
            ping: ctx.client().ping()
          });
        }
        """);

    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(
        Map.of("value", 1),
        output::add,
        scriptContext(scriptClientWithPing())
    );

    assertEquals(1, output.size());
    assertEquals("pong", output.get(0).get("ping"));
  }

  private static Map<String, Object> executeSingleEvent(org.apache.streampipes.connect.transformer.api.ScriptTransformer transformer,
                                                        Map<String, Object> input,
                                                        Context context)
      throws ScriptExecutionException {
    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(new LinkedHashMap<>(input), output::add, context);
    return output.get(0);
  }

  private static StreamPipesScriptContext scriptContext(Object scriptClient) {
    return new StreamPipesScriptContext(scriptClient);
  }

  private static Object scriptClientWithPing() {
    return ProxyObject.fromMap(Map.of(
        "ping", (ProxyExecutable) args -> "pong"
    ));
  }

  private static Object scriptClientWithAdapters(String elementId, String name) {
    Object adaptersApi = ProxyObject.fromMap(Map.of(
        "all", (ProxyExecutable) args -> List.of(Map.of(
            "elementId", elementId,
            "name", name
        ))
    ));
    return ProxyObject.fromMap(Map.of(
        "adapters", (ProxyExecutable) args -> adaptersApi
    ));
  }

  private static Object scriptClientWithCreateRecorder(List<Map<String, Object>> createdAdapters) {
    Object adaptersApi = ProxyObject.fromMap(Map.of(
        "create", (ProxyExecutable) args -> {
          try {
            createdAdapters.add(new LinkedHashMap<>(PolyglotTypeConverter.toEventMap(args[0])));
          } catch (ScriptExecutionException e) {
            throw new IllegalStateException("Failed to capture adapter payload", e);
          }
          return null;
        }
    ));
    return ProxyObject.fromMap(Map.of(
        "adapters", (ProxyExecutable) args -> adaptersApi
    ));
  }

  private static CredentialsProvider emptyCredentials() {
    return List::of;
  }

  private static StreamPipesClient offlineClient() {
    return StreamPipesClient.create("localhost", 1, emptyCredentials(), true);
  }
}
