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
package org.apache.streampipes.processors.llm.jvm.processor.multimodel;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TestMultiModelPromptProcessor {

  private MultiModelPromptProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new MultiModelPromptProcessor();
  }

  @Test
  @Disabled("Disabled because of Anthropic ApiKey")
  public void testLLM() {
    TestConfiguration config = TestConfiguration.builder()
            .configWithDefaultPrefix(MultiModelPromptProcessor.MAPPING_INPUT_ID, "userInput")
            .config(MultiModelPromptProcessor.MODEL_PROVIDER_ID, "Anthropic")
            .config(MultiModelPromptProcessor.MODEL_NAME_ID, "claude-3-5-sonnet-20240620")
            .config(MultiModelPromptProcessor.SYSTEM_PROMPT_ID, "In this chat, for each input number, calculate"
                    + " and return the current average. Do not output any other information than the answer.")
            .config(MultiModelPromptProcessor.ANTHROPIC_KEY_ID, "")
            .config(MultiModelPromptProcessor.HISTORY_MODE_ID, "Windowed")
            .config(MultiModelPromptProcessor.WINDOW_SIZE_ID, 2)
            .config(MultiModelPromptProcessor.TEMPERATURE, 0.1)
            .build();

    Consumer<DataProcessorInvocation> invocationConfig = (invocation -> {
      // Select Anthropic (option 2 (index 1)) as the selected model provider.
      List<StaticProperty> staticProperties = invocation.getStaticProperties();
      StaticPropertyAlternatives modelAlternative = (StaticPropertyAlternatives) staticProperties.get(1);
      modelAlternative.getAlternatives().get(1).setSelected(true);
    });

    List<Map<String, Object>> inputEvents = List.of(
            Map.of("userInput", "1"),
            Map.of("userInput", "2"),
            Map.of("userInput", "3")
    );

    List<Map<String, Object>> outputEvents = List.of(
            Map.of("userInput", "1", "llmOutput", "1"),
            Map.of("userInput", "2", "llmOutput", "1.5"),
            Map.of("userInput", "3", "llmOutput", "2")
    );

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, config, invocationConfig);

    testExecutor.run(inputEvents, outputEvents);
  }
}
