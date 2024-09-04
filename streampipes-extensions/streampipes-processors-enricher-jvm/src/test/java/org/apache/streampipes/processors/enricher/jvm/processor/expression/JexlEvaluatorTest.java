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
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  static Stream<Arguments> provideTestArguments() {
    MapContext context = new MapContext();
    context.set("a", 10);
    context.set("b", 20.0);
    context.set("c", 2);
    context.set("Math", Math.class);

    return Stream.of(
        Arguments.of(number("result1", "a + 5"), 15, context),
        Arguments.of(number("result1", "b + 5"), 25.0, context),
        Arguments.of(number("result1", "a + b * 5"), 110.0, context),
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
