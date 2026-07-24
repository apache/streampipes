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

package org.apache.streampipes.manager.execution.http;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class PipelineElementRollbackTest {

  @Test
  void extensionResponseUsesRuntimeElementIdInStatus() {
    var request = new TestPipelineElementExtensionRequest();
    var element = makeElement("urn:streampipes:runtime-element", "app-id", "service-id", "Element");

    var status = request.execute(element, "pipeline-id");

    assertEquals("urn:streampipes:runtime-element", status.getElementId());
  }

  @Test
  void rollbackDetachesSuccessfullyStartedElementByRuntimeElementId() {
    var startedElement = makeElement("urn:streampipes:started", "app-started", "service-id", "Started");
    var failedElement = makeElement("urn:streampipes:failed", "app-failed", "service-id", "Failed");
    var submitter = new TestInvokePipelineElementSubmitter(
        Map.of(
            startedElement.getElementId(), new PipelineElementStatus(startedElement.getElementId(),
                startedElement.getName(), true, ""),
            failedElement.getElementId(), new PipelineElementStatus(failedElement.getElementId(),
                failedElement.getName(), false, "startup failed")
        )
    );

    var status = submitter.submit(List.of(startedElement, failedElement));

    assertFalse(status.isSuccess());
    assertEquals(List.of(startedElement), submitter.detachedElements);
  }

  private static DataSinkInvocation makeElement(String elementId,
                                                String belongsTo,
                                                String selectedServiceId,
                                                String name) {
    var element = new DataSinkInvocation();
    element.setElementId(elementId);
    element.setBelongsTo(belongsTo);
    element.setSelectedServiceId(selectedServiceId);
    element.setName(name);
    return element;
  }

  private static class TestPipelineElementExtensionRequest extends PipelineElementExtensionRequest {

    TestPipelineElementExtensionRequest() {
      super(mock(ExtensionServiceRequestManager.class), mock(SpResourceManager.class));
    }

    @Override
    protected ExtensionServiceOperationResult performRequest(InvocableStreamPipesEntity pipelineElement,
                                                             String pipelineId) throws IOException {
      var body = """
          {"elementId":"urn:streampipes:runtime-element","success":true,"optionalMessage":""}
          """.getBytes(StandardCharsets.UTF_8);
      return new ExtensionServiceOperationResult(200, body);
    }

    @Override
    protected void logError(String endpointUrl,
                            String pipelineElementName,
                            String exceptionMessage) {

    }
  }

  private static class TestInvokePipelineElementSubmitter extends InvokePipelineElementSubmitter {

    private final Map<String, PipelineElementStatus> responses;
    private final List<InvocableStreamPipesEntity> detachedElements;

    TestInvokePipelineElementSubmitter(Map<String, PipelineElementStatus> responses) {
      super(makePipeline(), mock(ExtensionServiceRequestManager.class), mock(SpResourceManager.class));
      this.responses = responses;
      this.detachedElements = new ArrayList<>();
    }

    @Override
    protected PipelineElementStatus submitElement(InvocableStreamPipesEntity pipelineElement) {
      return responses.get(pipelineElement.getElementId());
    }

    @Override
    protected PipelineElementStatus performDetach(InvocableStreamPipesEntity pipelineElement) {
      detachedElements.add(pipelineElement);
      return new PipelineElementStatus(pipelineElement.getElementId(), pipelineElement.getName(), true, "");
    }

    private static Pipeline makePipeline() {
      var pipeline = new Pipeline();
      pipeline.setPipelineId("pipeline-id");
      pipeline.setName("Pipeline");
      return pipeline;
    }
  }
}
