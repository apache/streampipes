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

package org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean;

import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TransformToBooleanProcessorTest {

  @Test
  void onEvent() {

    var processor = new TransformToBooleanProcessor();

    List<Map<String, Object>> inputEvents = List.of(Map.of(
        "numberBoolean", 0,
        "stringBoolean", "true",
        "stringBooleanFalse", "false",
        "floatBoolean", 1.0
    ));

    List<Map<String, Object>> outputEvents = List.of(Map.of(
        "numberBoolean", false,
        "stringBoolean", true,
        "stringBooleanFalse", false,
        "floatBoolean", true
    ));

    var configuration = TestConfiguration
        .builder()
        .config(TransformToBooleanProcessor.TRANSFORM_FIELDS_ID, List.of(
            "s0::numberBoolean", "s0::stringBoolean", "s0::stringBooleanFalse", "s0::floatBoolean"))
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }
}
