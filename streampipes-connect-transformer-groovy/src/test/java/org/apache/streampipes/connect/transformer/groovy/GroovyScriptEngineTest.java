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

package org.apache.streampipes.connect.transformer.groovy;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.api.IAdapterApi;
import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.client.api.credentials.CredentialsProvider;
import org.apache.streampipes.connect.transformer.api.Context;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroovyScriptEngineTest {

  private final GroovyScriptEngine engine = new GroovyScriptEngine();

  @Test
  void compileAndExecuteSimplePassThroughScript() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("out.collect(input)");
    var output = new ArrayList<Map<String, Object>>();

    transformer.transform(
        new LinkedHashMap<>(Map.of("sensor", "machine-1", "value", 42)),
        output::add,
        null
    );

    assertEquals(1, output.size());
    assertEquals("machine-1", output.get(0).get("sensor"));
    assertEquals(42, output.get(0).get("value"));
  }

  @Test
  void compileAndExecuteFieldReshapingScript() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        out.collect([
            reading: [
                sensor: input.sensor,
                value: input.value + 1
            ],
            tags: [input.tag, "processed"]
        ])
        """);

    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(
        new LinkedHashMap<>(Map.of("sensor", "machine-2", "value", 9, "tag", "lab")),
        output::add,
        null
    );

    assertEquals(1, output.size());
    assertEquals(Map.of("sensor", "machine-2", "value", 10), output.get(0).get("reading"));
    assertEquals(java.util.List.of("lab", "processed"), output.get(0).get("tags"));
  }

  @Test
  void executeProvidesOfflineClientThroughContext() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        out.collect([
            hasClient: ctx.client() != null
        ])
        """);

    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(
        Map.of("value", 1),
        output::add,
        new GroovyScriptContext(offlineClient())
    );

    assertEquals(1, output.size());
    assertEquals(true, output.get(0).get("hasClient"));
  }

  @Test
  void executeProvidesContextClientCompatibility() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        out.collect([
            ping: ctx.client().ping()
        ])
        """);

    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(
        Map.of("value", 1),
        output::add,
        new ScriptContext(new ScriptClient())
    );

    assertEquals(1, output.size());
    assertEquals("pong", output.get(0).get("ping"));
  }

  @Test
  void executeCanReadClientResultsInsideVm() throws ScriptCompilationException, ScriptExecutionException {
    var transformer = engine.compile("""
        def adapters = ctx.client().adapters().all()
        out.collect([
            adapterId: adapters[0].elementId,
            adapterName: adapters[0].name
        ])
        """);

    var output = executeSingleEvent(
        transformer,
        Map.of("input", "value"),
        new GroovyScriptContext(scriptClientWithAdapters("adapter-groovy", "Groovy Adapter"))
    );

    assertEquals("adapter-groovy", output.get("adapterId"));
    assertEquals("Groovy Adapter", output.get("adapterName"));
  }

  @Test
  void executeCanSendGuestObjectToClient() throws ScriptCompilationException, ScriptExecutionException {
    var createdAdapters = new ArrayList<AdapterDescription>();
    var transformer = engine.compile("""
        def adapter = new org.apache.streampipes.model.connect.adapter.AdapterDescription()
        adapter.elementId = "created-groovy"
        adapter.name = "Created From Groovy"
        adapter.running = false
        ctx.client().adapters().create(adapter)
        out.collect([created: adapter.elementId])
        """);

    var output = executeSingleEvent(
        transformer,
        Map.of("input", "value"),
        new GroovyScriptContext(scriptClientWithCreateRecorder(createdAdapters))
    );

    assertEquals("created-groovy", output.get("created"));
    assertEquals(1, createdAdapters.size());
    assertEquals("created-groovy", createdAdapters.get(0).getElementId());
    assertEquals("Created From Groovy", createdAdapters.get(0).getName());
    assertFalse(createdAdapters.get(0).isRunning());
  }

  @Test
  void compileRejectsFileAccess() {
    assertSandboxViolation("new File('/tmp/blocked').text");
  }

  @Test
  void compileRejectsNioFileAccess() {
    assertSandboxViolation("Files.readString(Paths.get('/tmp/blocked'))");
  }

  @Test
  void compileRejectsEnvironmentAccess() {
    assertSandboxViolation("System.getenv('HOME')");
  }

  @Test
  void compileRejectsSystemExit() {
    assertSandboxViolation("System.exit(0)");
  }

  @Test
  void compileRejectsProcessExecution() {
    assertSandboxViolation("Runtime.getRuntime().exec('id')");
  }

  @Test
  void compileRejectsNetworkAccess() {
    assertSandboxViolation("new Socket('localhost', 80)");
  }

  @Test
  void compileRejectsExplicitDangerousImports() {
    assertSandboxViolation("""
        import java.lang.reflect.Method
        out.collect(input)
        """);
  }

  @Test
  void executeWrapsGroovyRuntimeErrors() throws ScriptCompilationException {
    var transformer = engine.compile("throw new IllegalStateException('boom')");

    var exception = assertThrows(
        ScriptExecutionException.class,
        () -> transformer.transform(Map.of("value", 1), event -> {
        }, null)
    );

    assertEquals("Groovy template execution failed", exception.getMessage());
    assertInstanceOf(Exception.class, exception.getCause());
  }

  private void assertSandboxViolation(String script) {
    var exception = assertThrows(
        ScriptCompilationException.class,
        () -> engine.compile(script)
    );

    assertTrue(exception.getMessage().contains("sandbox restrictions"));
  }

  private static Map<String, Object> executeSingleEvent(
      org.apache.streampipes.connect.transformer.api.ScriptTransformer transformer,
      Map<String, Object> input,
      Context context
  ) throws ScriptExecutionException {
    var output = new ArrayList<Map<String, Object>>();
    transformer.transform(new LinkedHashMap<>(input), output::add, context);
    return output.get(0);
  }

  private static CredentialsProvider emptyCredentials() {
    return List::of;
  }

  private static StreamPipesClient offlineClient() {
    return StreamPipesClient.create("localhost", 1, emptyCredentials(), true);
  }

  private static IStreamPipesClient scriptClientWithAdapters(String elementId, String name) {
    var adapter = new AdapterDescription();
    adapter.setElementId(elementId);
    adapter.setName(name);
    return scriptClient(adapterApi(List.of(adapter), null));
  }

  private static IStreamPipesClient scriptClientWithCreateRecorder(List<AdapterDescription> createdAdapters) {
    return scriptClient(adapterApi(List.of(), createdAdapters));
  }

  private static IStreamPipesClient scriptClient(IAdapterApi adapterApi) {
    return (IStreamPipesClient) Proxy.newProxyInstance(
        GroovyScriptEngineTest.class.getClassLoader(),
        new Class<?>[]{IStreamPipesClient.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "adapters" -> adapterApi;
          case "getCredentials" -> emptyCredentials();
          case "getConfig", "getConnectionConfig" -> null;
          case "toString" -> "ScriptClientProxy";
          case "hashCode" -> System.identityHashCode(proxy);
          case "equals" -> proxy == args[0];
          default -> throw new UnsupportedOperationException("Unsupported client method: " + method.getName());
        }
    );
  }

  private static IAdapterApi adapterApi(List<AdapterDescription> adapters, List<AdapterDescription> createdAdapters) {
    return (IAdapterApi) Proxy.newProxyInstance(
        GroovyScriptEngineTest.class.getClassLoader(),
        new Class<?>[]{IAdapterApi.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "all" -> adapters;
          case "create" -> {
            if (createdAdapters != null) {
              createdAdapters.add(new AdapterDescription((AdapterDescription) args[0]));
            }
            yield null;
          }
          case "get" -> java.util.Optional.empty();
          case "delete", "update" -> null;
          case "toString" -> "AdapterApiProxy";
          case "hashCode" -> System.identityHashCode(proxy);
          case "equals" -> proxy == args[0];
          default -> throw new UnsupportedOperationException("Unsupported adapter method: " + method.getName());
        }
    );
  }

  private static final class ScriptContext implements Context {

    private final ScriptClient client;

    private ScriptContext(ScriptClient client) {
      this.client = client;
    }

    public ScriptClient client() {
      return client;
    }
  }

  private static final class ScriptClient {

    public String ping() {
      return "pong";
    }
  }
}
