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
import org.apache.streampipes.manager.execution.HttpExtensionServiceRequestManager;
import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

public abstract class PipelineElementHttpRequest {

  private final ExtensionServiceRequestManager requestManager;

  public PipelineElementHttpRequest() {
    this(new HttpExtensionServiceRequestManager());
  }

  public PipelineElementHttpRequest(ExtensionServiceRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public PipelineElementStatus execute(EndpointSelectable pipelineElement,
                                       String endpointUrl,
                                       String pipelineId) {
    try {
      ExtensionServiceOperationResult response = performRequest(pipelineElement, endpointUrl, pipelineId);
      return handleResponse(response, pipelineElement, endpointUrl);
    } catch (Exception e) {
      logError(endpointUrl, pipelineElement.getName(), e.getMessage());
      return new PipelineElementStatus(endpointUrl, pipelineElement.getName(), false, e.getMessage());
    }
  }

  protected abstract ExtensionServiceOperationResult performRequest(EndpointSelectable pipelineElement,
                                                                    String endpointUrl,
                                                                    String pipelineId) throws IOException;

  protected abstract void logError(String endpointUrl,
                                String pipelineElementName,
                                String exceptionMessage);

  protected PipelineElementStatus handleResponse(ExtensionServiceOperationResult response,
                                                 EndpointSelectable pipelineElement,
                                                 String endpointUrl) throws JsonSyntaxException, IOException {
    if (!response.isSuccess()) {
      throw new IOException("Request failed with status code " + response.statusCode());
    }

    String resp = response.responseBody();
    org.apache.streampipes.model.Response streamPipesResp = JacksonSerializer
        .getObjectMapper()
        .readValue(resp, org.apache.streampipes.model.Response.class);
    return convert(streamPipesResp, endpointUrl, pipelineElement.getName());
  }

  private PipelineElementStatus convert(org.apache.streampipes.model.Response response,
                                        String endpointUrl,
                                        String pipelineElementName) {
    return new PipelineElementStatus(endpointUrl, pipelineElementName, response.isSuccess(),
        response.getOptionalMessage());
  }

  protected ExtensionServiceRequestManager requestManager() {
    return requestManager;
  }
}
