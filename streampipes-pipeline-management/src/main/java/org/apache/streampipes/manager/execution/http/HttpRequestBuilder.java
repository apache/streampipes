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


import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.serializers.json.JacksonSerializer;


import java.io.IOException;

public class HttpRequestBuilder {

  private final NamedStreamPipesEntity payload;
  private final String endpointUrl;

  private static final Integer CONNECT_TIMEOUT = 10000;

  private final static Logger LOG = LoggerFactory.getLogger(HttpRequestBuilder.class);

  public HttpRequestBuilder(NamedStreamPipesEntity payload, String endpointUrl) {
    this.payload = payload;
    this.endpointUrl = endpointUrl;
  }

  public PipelineElementStatus invoke() {
    try {
      if (payload instanceof InvocableStreamPipesEntity) {
        LOG.info("Invoking pipeline element: " + endpointUrl);
      } else {
        LOG.info("Invoking data stream relay: " + endpointUrl);
      }
      String json = toJson();
      Response httpResp = Request
              .Post(endpointUrl)
              .bodyString(json, ContentType.APPLICATION_JSON)
              .connectTimeout(CONNECT_TIMEOUT)
              .execute();
      return handleResponse(httpResp, "invoke");
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return new PipelineElementStatus(endpointUrl, payload.getName(), false, e.getMessage());
    }
  }

  public PipelineElementStatus detach() {
    if (payload instanceof InvocableStreamPipesEntity) {
      LOG.info("Detaching pipeline element: " + endpointUrl);
    } else {
      LOG.info("Detaching data stream relay: " + endpointUrl);
    }
    try {
      Response httpResp = Request
              .Delete(endpointUrl)
              .connectTimeout(CONNECT_TIMEOUT)
              .execute();
      return handleResponse(httpResp, "detach");
    } catch (Exception e) {
      LOG.error("Could not stop pipeline " + endpointUrl, e.getMessage());
      return new PipelineElementStatus(endpointUrl, payload.getName(), false, e.getMessage());
    }
  }

  private PipelineElementStatus handleResponse(Response httpResp, String action) throws JsonSyntaxException, IOException {
    String resp = httpResp.returnContent().asString();
    org.apache.streampipes.model.Response streamPipesResp = JacksonSerializer
            .getObjectMapper()
            .readValue(resp, org.apache.streampipes.model.Response.class);
    return convert(streamPipesResp, action);
  }

  private PipelineElementStatus convert(org.apache.streampipes.model.Response response, String action) {
    PipelineElementStatus status = new PipelineElementStatus(endpointUrl, payload.getName(), response.isSuccess(),
            response.getOptionalMessage());
    if(payload instanceof InvocableStreamPipesEntity){
      status.setElementNode(((InvocableStreamPipesEntity)payload).getDeploymentTargetNodeId());
      status.setOperation(action);
    }
    else if(payload instanceof SpDataStreamRelayContainer){
      status.setElementNode(((SpDataStreamRelayContainer)payload).getDeploymentTargetNodeId());
      status.setOperation(action + " relay");
    }
    return status;
  }

  private String toJson() throws Exception {
    return JacksonSerializer.getObjectMapper().writeValueAsString(payload);
  }
}
