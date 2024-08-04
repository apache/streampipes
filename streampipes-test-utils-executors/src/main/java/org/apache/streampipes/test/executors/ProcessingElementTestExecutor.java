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

package org.apache.streampipes.test.executors;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.manager.template.DataProcessorTemplateHandler;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.test.generator.EventStreamGenerator;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ProcessingElementTestExecutor {

  private final IStreamPipesDataProcessor processor;
  private final TestConfiguration testConfiguration;
  private Iterator<String> selectorPrefixes;
  private Consumer<DataProcessorInvocation> invocationConfig;

  public ProcessingElementTestExecutor(
      IStreamPipesDataProcessor processor, TestConfiguration testConfiguration,
      Consumer<DataProcessorInvocation> invocationConfig
  ) {
    this.processor = processor;
    this.testConfiguration = testConfiguration;
    this.selectorPrefixes = testConfiguration.getPrefixes()
                                             .iterator();
    this.invocationConfig = invocationConfig;
  }

  public ProcessingElementTestExecutor(IStreamPipesDataProcessor processor, TestConfiguration testConfiguration) {
    this.processor = processor;
    this.testConfiguration = testConfiguration;
    this.selectorPrefixes = testConfiguration.getPrefixes()
                                             .iterator();
  }

  /**
   * This method is used to run a data processor with a given configuration and a list of input events.
   * It then verifies the output events against the expected output events.
   *
   * @param inputEvents          The list of input events to be processed.
   * @param expectedOutputEvents The list of expected output events.
   */
  public void run(
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> expectedOutputEvents
  ) {


    // initialize the extractor with the provided configuration of the user input
    var dataProcessorInvocation = getProcessorInvocation();
    if (invocationConfig != null) {
      invocationConfig.accept(dataProcessorInvocation);
    }

    var extractor = getProcessingElementParameterExtractor(dataProcessorInvocation);
    var mockParams = mock(IDataProcessorParameters.class);

    when(mockParams.getModel()).thenReturn(dataProcessorInvocation);
    when(mockParams.extractor()).thenReturn(extractor);

    // calls the onPipelineStarted method of the processor to initialize it
    processor.onPipelineStarted(mockParams, null, null);

    // mock the output collector to capture the output events and validate the results later
    var mockCollector = mock(SpOutputCollector.class);
    var spOutputCollectorCaptor = ArgumentCaptor.forClass(Event.class);


    // Iterate over all input events and call the onEvent method of the processor
    for (Map<String, Object> inputRawEvent : inputEvents) {
      processor.onEvent(getEvent(inputRawEvent), mockCollector);
    }

    // Validate the output of the processor
    Mockito.verify(
               mockCollector,
               Mockito.times(expectedOutputEvents.size())
           )
           .collect(spOutputCollectorCaptor.capture());
    var resultingEvents = spOutputCollectorCaptor.getAllValues();
    IntStream.range(0, expectedOutputEvents.size())
             .forEach(i -> assertEventEquality(
                 expectedOutputEvents.get(i),
                 resultingEvents.get(i)
                                .getRaw()
             ));

    // validate that the processor is stopped correctly
    processor.onPipelineStopped();
  }

  /**
   * Asserts the equality of expected and actual event.
   * Iterates through each key-value pair in the expected map and compares the value
   * with the corresponding value in the actual map.
   *
   * @param expected The expected event
   * @param actual   The actual event
   */
  private void assertEventEquality(Map<String, Object> expected, Map<String, Object> actual) {
    expected.forEach((key, value) -> {
      if (value instanceof Approx roundValue) {
        assertEquals(
            roundValue.value(),
            (Double) actual.get(key),
            roundValue.epsilon(),
            () -> getAssertionErrorMessage(key, expected, actual));
      } else {
        assertEquals(
            value,
            actual.get(key),
            () -> getAssertionErrorMessage(key, expected, actual)
        );
      }

    });
  }

  private String getAssertionErrorMessage(String key, Map<String, Object> expected, Map<String, Object> actual) {
    return "Assertion failed for key: %s. Expected: %s, Actual: %s".formatted(key, expected, actual);
  }

  private static ProcessingElementParameterExtractor getProcessingElementParameterExtractor(
      DataProcessorInvocation dataProcessorInvocation
  ) {
    return ProcessingElementParameterExtractor.from(dataProcessorInvocation);
  }

  private DataProcessorInvocation getProcessorInvocation() {
    var pipelineElementTemplate = getPipelineElementTemplate();

    var invocation = new DataProcessorInvocation(
        processor
            .declareConfig()
            .getDescription()
    );

    invocation.setOutputStream(EventStreamGenerator.makeEmptyStream());


    return new DataProcessorTemplateHandler(
        pipelineElementTemplate,
        invocation,
        true
    )
        .applyTemplateOnPipelineElement();
  }

  private PipelineElementTemplate getPipelineElementTemplate() {
    var staticProperties = processor
        .declareConfig()
        .getDescription()
        .getStaticProperties();


    var configs = new HashMap<String, PipelineElementTemplateConfig>();

    staticProperties.forEach(staticProperty -> {
      var value = testConfiguration.getFieldConfiguration()
                                   .get(staticProperty.getInternalName());
      configs.put(
          staticProperty.getInternalName(),
          new PipelineElementTemplateConfig(true, true, value)
      );
    });

    return new PipelineElementTemplate("name", "description", configs);
  }

  private Event getEvent(Map<String, Object> rawEvent) {

    if (!selectorPrefixes.hasNext()) {
      selectorPrefixes = testConfiguration.getPrefixes()
                                          .iterator();
    }

    String selectorPrefix = selectorPrefixes.next();


    var sourceInfo = new SourceInfo("", selectorPrefix);
    var schemaInfo = new SchemaInfo(null, new ArrayList<>());

    return EventFactory.fromMap(rawEvent, sourceInfo, schemaInfo);
  }
}
