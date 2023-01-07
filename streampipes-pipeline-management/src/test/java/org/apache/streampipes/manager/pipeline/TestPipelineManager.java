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

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.test.generator.pipeline.DummyPipelineGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    PipelineManager.class,
    Operations.class})
public class TestPipelineManager {

  @Test
  public void testStartPipeline() {
    // Prepare
    PipelineOperationStatus expectedPipelineOperationStatus = getPipelineOperationStatus();
    PowerMockito.stub(PowerMockito.method(PipelineManager.class, "getPipeline", String.class))
        .toReturn(DummyPipelineGenerator.makePipelineWithPipelineName());
    PowerMockito.stub(PowerMockito.method(Operations.class, "startPipeline", Pipeline.class))
        .toReturn(expectedPipelineOperationStatus);

    // Test
    PipelineOperationStatus result = PipelineManager.startPipeline("pipelineId");

    // Assertions
    assertNotNull(result);
    assertEquals(expectedPipelineOperationStatus.getPipelineName(), result.getPipelineName());
  }

  @Test
  public void testStopPipeline() {
    // Prepare
    PipelineOperationStatus expectedPipelineOperationStatus = getPipelineOperationStatus();
    PowerMockito.stub(PowerMockito.method(PipelineManager.class, "getPipeline", String.class))
        .toReturn(DummyPipelineGenerator.makePipelineWithPipelineName());
    PowerMockito.stub(PowerMockito.method(Operations.class, "stopPipeline", Pipeline.class, boolean.class))
        .toReturn(expectedPipelineOperationStatus);

    // Test
    PipelineOperationStatus result = PipelineManager.stopPipeline("pipelineId", true);

    // Assertions
    assertNotNull(result);
    assertEquals(expectedPipelineOperationStatus.getPipelineName(), result.getPipelineName());
  }


  private PipelineOperationStatus getPipelineOperationStatus() {
    return new PipelineOperationStatus("", DummyPipelineGenerator.PIPELINE_NAME, "", new ArrayList<>());
  }
}
