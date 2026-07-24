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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.util.AuthTokenProvider;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InvokeExtensionRequest extends PipelineElementExtensionRequest {

  private static final Logger LOG = LoggerFactory.getLogger(InvokeExtensionRequest.class);

  public InvokeExtensionRequest(ExtensionServiceRequestManager requestManager,
                                SpResourceManager resourceManager) {
    super(requestManager, resourceManager);
  }

  @Override
  protected ExtensionServiceOperationResult performRequest(InvocableStreamPipesEntity pipelineElement,
                                                           String pipelineId) throws IOException {
    LOG.info("Invoking element: " + pipelineElement.getSelectedServiceId());
    var provider = ExtensionsServiceEndpointUtils.getPipelineElementType(pipelineElement);
    var requestTarget = ExtensionServiceRequestTargets.pipelineInvocation(
        pipelineElement.getSelectedEndpointUrl(),
        pipelineElement.getSelectedServiceId(),
        provider,
        pipelineElement.getAppId()
    );
    var authToken = new AuthTokenProvider(resourceManager).getAuthToken(pipelineId);
    return requestManager().request(
        ExtensionServiceRequests
            .pipelineElementInvocation(requestTarget, toJson(pipelineElement, pipelineId), authToken)
    );
  }

  @Override
  protected void logError(String endpointUrl,
                       String pipelineElementName,
                       String exceptionMessage) {
    LOG.error("Could not perform invocation request at {} for pipeline element {}: {}",
        endpointUrl, pipelineElementName, exceptionMessage);
  }

  String toJson(InvocableStreamPipesEntity pipelineElement,
                String pipelineId) throws JsonProcessingException {
    var invocation = makeInvocationPayload(pipelineElement);
    if (pipelineId != null) {
      invocation.setCorrespondingUser(getPipelineOwnerSid(pipelineId));
    }
    return JacksonSerializer.getObjectMapper().writeValueAsString(invocation);
  }

  private InvocableStreamPipesEntity makeInvocationPayload(InvocableStreamPipesEntity pipelineElement) {
    InvocableStreamPipesEntity invocation;
    if (pipelineElement instanceof DataProcessorInvocation processorInvocation) {
      invocation = new DataProcessorInvocation(processorInvocation);
    } else if (pipelineElement instanceof DataSinkInvocation sinkInvocation) {
      invocation = new DataSinkInvocation(sinkInvocation);
    } else {
      throw new IllegalArgumentException("Unsupported pipeline element type: "
          + pipelineElement.getClass().getCanonicalName());
    }
    invocation.setSelectedServiceId(pipelineElement.getSelectedServiceId());
    return invocation;
  }

  private String getPipelineOwnerSid(String pipelineId) {
    return resourceManager.managePermissions().findForObjectId(pipelineId)
        .stream()
        .findFirst()
        .map(Permission::getOwnerSid)
        .orElseThrow(() -> new IllegalArgumentException("Could not find owner for pipeline " + pipelineId));
  }
}
