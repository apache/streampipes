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

package org.apache.streampipes.processors.enricher.jvm.processor.expression;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.SO;

import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JexlEvaluatorTest {

  private JexlEngine engine;

  @BeforeEach
  void setup() {
    this.engine = new JexlEngineProvider().getEngine();
  }

  @ParameterizedTest
  @MethodSource("provideTestArguments")
  void testEvaluate(JexlDescription jexlDescription,
                    Object expectedResult,
                    MapContext context) {
    var evaluator = new JexlEvaluator(jexlDescription, engine);
    var event = makeBaseEvent();
    evaluator.evaluate(context, event);

    assertEquals(4, event.getRaw().size());
    assertEquals(expectedResult, event.getRaw().get("result1"));

  }

  @ParameterizedTest
  @ValueSource(strings = {
      "while (true) { a }",
      "for (var i : [1, 2, 3]) { a }",
      "do { a } while (true)"
  })
  void rejectsLoops(String script) {
    assertThrows(JexlException.class, () -> new JexlEvaluator(number("result1", script), engine));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "a = 5",
      "a += 1",
      "a++",
      "x = 1"
  })
  void rejectsSideEffects(String script) {
    assertThrows(JexlException.class, () -> new JexlEvaluator(number("result1", script), engine));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "a +",
      "Math.pow(a, c",
      "a b"
  })
  void rejectsInvalidSyntax(String script) {
    assertThrows(JexlException.class, () -> new JexlEvaluator(number("result1", script), engine));
  }

  /**
   * Reflection through java.lang.Class is denied by the permissions; JEXL resolves the call to null
   * instead of throwing, so no Class, ClassLoader or Runtime instance leaks into the event.
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "Math.forName('java.lang.Runtime')",
      "Math.forName('java.lang.Runtime').getRuntime()",
      "Math.getClassLoader()",
      "''.getClass().forName('java.lang.Runtime')"
  })
  void deniesReflectionThroughClass(String script) {
    var evaluator = new JexlEvaluator(number("result1", script), engine);
    var event = makeBaseEvent();

    evaluator.evaluate(makeContext(), event);

    assertNull(event.getRaw().get("result1"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "new('java.lang.ProcessBuilder', 'id')",
      "new('java.io.File', '/tmp/x').delete()"
  })
  void rejectsInstantiationOutsideMath(String script) {
    var evaluator = new JexlEvaluator(number("result1", script), engine);
    var event = makeBaseEvent();

    assertThrows(JexlException.class, () -> evaluator.evaluate(makeContext(), event));
    assertFalse(event.getRaw().containsKey("result1"));
  }

  @Test
  void rejectsUndefinedVariable() {
    var evaluator = new JexlEvaluator(number("result1", "d + 1"), engine);
    var event = makeBaseEvent();

    assertThrows(JexlException.class, () -> evaluator.evaluate(makeContext(), event));
    assertFalse(event.getRaw().containsKey("result1"));
  }

  private static MapContext makeContext() {
    MapContext context = new MapContext();
    context.set("a", 10);
    context.set("b", 20.0);
    context.set("c", 2);
    context.set("Math", Math.class);
    return context;
  }

  static Stream<Arguments> provideTestArguments() {
    MapContext context = makeContext();

    return Stream.of(
        Arguments.of(number("result1", "a + 5"), 15, context),
        Arguments.of(number("result1", "b + 5"), 25.0, context),
        Arguments.of(number("result1", "a + b * 5"), 110.0, context),
        Arguments.of(number("result1", "Math.pow(a, c)"), 100.0, context)
    );
  }

  private static JexlDescription number(String runtimeName,
                                      String script) {
    return new JexlDescription(numberEp(runtimeName), script);
  }

  private static EventPropertyPrimitive numberEp(String runtimeName) {
    return EpProperties.doubleEp(Labels.empty(), runtimeName, SO.NUMBER);
  }

  private static Event makeBaseEvent() {
    var event = new Event();
    event.addField("a", 10);
    event.addField("b", 20.0);
    event.addField("c", 2);
    return event;
  }


}
