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

import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import java.io.IOException;

public abstract class PipelineElementHttpRequest {

  public PipelineElementStatus execute(EndpointSelectable pipelineElement,
                                       String endpointUrl,
                                       String pipelineId) {
    try {
      Response httpResp = initRequest(pipelineElement, endpointUrl)
              .addHeader("Authorization", AuthTokenUtils.getAuthToken(pipelineId))
              .connectTimeout(10000)
              .execute();
      return handleResponse(httpResp, pipelineElement, endpointUrl);
    } catch (Exception e) {
      logError(endpointUrl, pipelineElement.getName(), e.getMessage());
      return new PipelineElementStatus(endpointUrl, pipelineElement.getName(), false, e.getMessage());
    }
  }

  protected abstract Request initRequest(EndpointSelectable pipelineElement,
                                      String endpointUrl) throws JsonProcessingException;

  protected abstract void logError(String endpointUrl,
                                String pipelineElementName,
                                String exceptionMessage);

  protected PipelineElementStatus handleResponse(Response httpResp,
                                                 EndpointSelectable pipelineElement,
                                                 String endpointUrl) throws JsonSyntaxException, IOException {
    String resp = httpResp.returnContent().asString();
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
}
