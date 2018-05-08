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
package org.streampipes.manager.matching.output;

import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomTransformOutputStrategy;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.serializers.json.GsonSerializer;

import java.io.IOException;

public class CustomTransformOutputSchemaGenerator implements OutputSchemaGenerator<CustomTransformOutputStrategy> {

  private DataProcessorInvocation dataProcessorInvocation;
  private CustomTransformOutputStrategy outputStrategy;

  public CustomTransformOutputSchemaGenerator(DataProcessorInvocation dataProcessorInvocation, CustomTransformOutputStrategy firstOutputStrategy) {
    this.dataProcessorInvocation = dataProcessorInvocation;
    this.outputStrategy = firstOutputStrategy;
  }

  @Override
  public EventSchema buildFromOneStream(SpDataStream stream) {
    return makeRequest();
  }

  @Override
  public EventSchema buildFromTwoStreams(SpDataStream stream1, SpDataStream stream2) {
    return makeRequest();
  }

  @Override
  public CustomTransformOutputStrategy getModifiedOutputStrategy(CustomTransformOutputStrategy outputStrategy) {
    return outputStrategy;
  }

  private EventSchema makeRequest() {
    String httpRequestBody = GsonSerializer.getGsonWithIds().toJson(dataProcessorInvocation);

    try {
      Response httpResp = Request.Post(dataProcessorInvocation.getBelongsTo() + "/output").bodyString(httpRequestBody,
              ContentType
              .APPLICATION_JSON).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      e.printStackTrace();
      return new EventSchema();
    }
  }

  private EventSchema handleResponse(Response httpResp) throws JsonSyntaxException, IOException {
    String resp = httpResp.returnContent().asString();
    EventSchema outputSchema = GsonSerializer
            .getGsonWithIds()
            .fromJson(resp, EventSchema.class);

    return outputSchema;
  }
}
