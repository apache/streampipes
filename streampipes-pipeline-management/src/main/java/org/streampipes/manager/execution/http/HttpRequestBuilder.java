/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.execution.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.Utils;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.pipeline.PipelineElementStatus;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import java.io.IOException;

public class HttpRequestBuilder {

  private NamedStreamPipesEntity payload;
  private String belongsTo;

  private final static Logger LOG = LoggerFactory.getLogger(HttpRequestBuilder.class);

  public HttpRequestBuilder(NamedStreamPipesEntity payload, String belongsTo) {
    this.payload = payload;
    this.belongsTo = belongsTo;
  }

  public PipelineElementStatus invoke() {
    LOG.info("Invoking element: " + belongsTo);
    try {
			Response httpResp = Request.Post(belongsTo).bodyString(jsonLd(), ContentType.APPLICATION_JSON).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return new PipelineElementStatus(belongsTo, payload.getName(), false, e.getMessage());
    }
  }

  public PipelineElementStatus detach() {
    try {
      Response httpResp = Request.Delete(belongsTo).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      LOG.error("Could not stop pipeline " + belongsTo, e.getMessage());
      return new PipelineElementStatus(belongsTo, payload.getName(), false, e.getMessage());
    }
  }

  private PipelineElementStatus handleResponse(Response httpResp) throws JsonSyntaxException, ClientProtocolException, IOException {
    String resp = httpResp.returnContent().asString();
    org.streampipes.model.Response streamPipesResp = new Gson().fromJson(resp, org.streampipes.model.Response.class);
    return convert(streamPipesResp);
  }

  private String jsonLd() throws Exception {
    return Utils.asString(new JsonLdTransformer().toJsonLd(payload));
  }

  private PipelineElementStatus convert(org.streampipes.model.Response response) {
    return new PipelineElementStatus(belongsTo, payload.getName(), response.isSuccess(), response.getOptionalMessage());
  }
}
