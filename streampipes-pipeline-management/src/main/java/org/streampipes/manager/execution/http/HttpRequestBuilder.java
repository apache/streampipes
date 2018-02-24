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
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.PipelineElementStatus;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import java.io.IOException;

public class HttpRequestBuilder {

  private InvocableStreamPipesEntity payload;

  private final static Logger LOG = LoggerFactory.getLogger(HttpRequestBuilder.class);

  public HttpRequestBuilder(InvocableStreamPipesEntity payload) {
    this.payload = payload;
  }

  public PipelineElementStatus invoke() {
    LOG.info("Invoking element: " + payload.getBelongsTo());
    try {
			Response httpResp = Request.Post(payload.getBelongsTo()).bodyString(jsonLd(), ContentType.APPLICATION_JSON).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), false, e.getMessage());
    }
  }

  public PipelineElementStatus detach() {
    try {
      Response httpResp = Request.Delete(payload.getUri()).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), false, e.getMessage());
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
    return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), response.isSuccess(), response.getOptionalMessage());
  }
}
