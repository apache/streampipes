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

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class DetachExtensionRequest extends PipelineElementExtensionRequest {

  private static final Logger LOG = LoggerFactory.getLogger(DetachExtensionRequest.class);

  @Override
  protected ExtensionServiceOperationResult performRequest(InvocableStreamPipesEntity pipelineElement,
                                                           String pipelineId) throws IOException {
    LOG.info("Detaching element {}", pipelineElement.getName());
    var provider = ExtensionsServiceEndpointUtils.getPipelineElementType(pipelineElement);
    var instanceId = InstanceIdExtractor.extractId(pipelineElement.getElementId());
    var requestTarget = ExtensionServiceRequestTargets.pipelineDetach(
        pipelineElement.getSelectedEndpointUrl(),
        pipelineElement.getSelectedServiceId(),
        provider,
        pipelineElement.getAppId(),
        instanceId
    );
    return requestManager().requestPipelineElementDetach(requestTarget, pipelineId);
  }

  @Override
  protected void logError(String endpointUrl, String pipelineElementName, String exceptionMessage) {
    LOG.error("Could not stop pipeline element {} at {}: {}", endpointUrl, pipelineElementName, exceptionMessage);
  }
}
