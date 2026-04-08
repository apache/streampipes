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

import org.apache.streampipes.connect.transformer.api.Context;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
