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
package org.apache.streampipes.manager.pipeline;

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.test.generator.pipeline.DummyPipelineGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    PipelineManager.class})
public class TestPipelinesContainingElements {
  public static final String TARGET_ELEMENT_ID = "TARGET_ELEMENT_ID";

  private Pipeline pipeline;

  @Before
  public void before() {
    pipeline = DummyPipelineGenerator.makePipelineWithProcessorAndSink();
    PowerMockito.stub(PowerMockito.method(PipelineManager.class, "getAllPipelines"))
        .toReturn(Collections.singletonList(pipeline));
  }

  @Test
  public void getPipelineContainingNoElement() {
    List<Pipeline> resultingPipelines = PipelineManager.getPipelinesContainingElements("");
    assertNotNull(resultingPipelines);
    assertEquals(resultingPipelines.size(), 0);
  }

  @Test
  public void getPipelineContainingStream() {
    pipeline.getStreams().forEach((spDataStream) -> spDataStream.setElementId(TARGET_ELEMENT_ID));

    validateOneResultingPipeline();
  }


  @Test
  public void getPipelineContainingProcessor() {

    pipeline.getSepas().forEach((processor) -> processor.setElementId(TARGET_ELEMENT_ID));

    validateOneResultingPipeline();
  }

  @Test
  public void getPipelineContainingSink() {

    pipeline.getActions().forEach((sink) -> sink.setElementId(TARGET_ELEMENT_ID));

    validateOneResultingPipeline();
  }

  private void validateOneResultingPipeline() {
    List<Pipeline> resultingPipelines = PipelineManager.getPipelinesContainingElements(TARGET_ELEMENT_ID);
    assertNotNull(resultingPipelines);
    assertEquals(resultingPipelines.size(), 1);
    assertEquals(resultingPipelines.get(0).getName(), DummyPipelineGenerator.PIPELINE_NAME);
  }

}
