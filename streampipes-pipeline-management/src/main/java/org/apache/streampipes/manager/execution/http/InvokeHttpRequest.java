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
import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InvokeHttpRequest extends PipelineElementHttpRequest {

  private static final Logger LOG = LoggerFactory.getLogger(InvokeHttpRequest.class);

  @Override
  protected ExtensionServiceOperationResult performRequest(EndpointSelectable pipelineElement,
                                                           String endpointUrl,
                                                           String pipelineId) throws IOException {
    LOG.info("Invoking element: " + endpointUrl);
    return requestManager().requestPipelineElementInvocation(endpointUrl, pipelineId, toJson(pipelineElement));
  }

  @Override
  protected void logError(String endpointUrl,
                       String pipelineElementName,
                       String exceptionMessage) {
    LOG.error("Could not perform invocation request at {} for pipeline element {}: {}",
        endpointUrl, pipelineElementName, exceptionMessage);
  }

  private String toJson(EndpointSelectable pipelineElement) throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().writeValueAsString(pipelineElement);
  }
}
