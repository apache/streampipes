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
import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpRequestBuilder {

  private final NamedStreamPipesEntity payload;
  private final String endpointUrl;
  private String pipelineId;

  private final static Logger LOG = LoggerFactory.getLogger(HttpRequestBuilder.class);

  public HttpRequestBuilder(NamedStreamPipesEntity payload,
                            String endpointUrl,
                            String pipelineId) {
    this.payload = payload;
    this.endpointUrl = endpointUrl;
    this.pipelineId = pipelineId;
  }

  public PipelineElementStatus invoke() {
    LOG.info("Invoking element: " + endpointUrl);
    try {
      String jsonDocument = toJson();
      Response httpResp =
              Request.Post(endpointUrl)
                      .addHeader("Authorization", AuthTokenUtils.getAuthToken(this.pipelineId))
                      .bodyString(jsonDocument, ContentType.APPLICATION_JSON)
                      .connectTimeout(10000)
                      .execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      LOG.error("Could not perform invocation request", e);
      return new PipelineElementStatus(endpointUrl, payload.getName(), false, e.getMessage());
    }
  }

  public PipelineElementStatus detach() {
    try {
      Response httpResp = Request.Delete(endpointUrl)
              .addHeader("Authorization", AuthTokenUtils.getAuthToken(this.pipelineId))
              .connectTimeout(10000).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      LOG.error("Could not stop pipeline {}", endpointUrl, e);
      return new PipelineElementStatus(endpointUrl, payload.getName(), false, e.getMessage());
    }
  }

  private PipelineElementStatus handleResponse(Response httpResp) throws JsonSyntaxException, IOException {
    String resp = httpResp.returnContent().asString();
    org.apache.streampipes.model.Response streamPipesResp = JacksonSerializer
            .getObjectMapper()
            .readValue(resp, org.apache.streampipes.model.Response.class);
    return convert(streamPipesResp);
  }

  private String toJson() throws Exception {
    return JacksonSerializer.getObjectMapper().writeValueAsString(payload);
  }

  private PipelineElementStatus convert(org.apache.streampipes.model.Response response) {
    return new PipelineElementStatus(endpointUrl, payload.getName(), response.isSuccess(), response.getOptionalMessage());
  }


}
