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
package org.apache.streampipes.manager.matching.output;

import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomTransformOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class CustomTransformOutputSchemaGenerator extends OutputSchemaGenerator<CustomTransformOutputStrategy> {

  private DataProcessorInvocation dataProcessorInvocation;
  private CustomTransformOutputStrategy outputStrategy;

  public static CustomTransformOutputSchemaGenerator from(OutputStrategy strategy, DataProcessorInvocation invocation) {
    return new CustomTransformOutputSchemaGenerator((CustomTransformOutputStrategy) strategy, invocation);
  }

  public CustomTransformOutputSchemaGenerator(CustomTransformOutputStrategy strategy,
                                              DataProcessorInvocation invocation) {
    super(strategy);
    this.dataProcessorInvocation = invocation;
  }


  @Override
  public Tuple2<EventSchema, CustomTransformOutputStrategy> buildFromOneStream(SpDataStream stream) {
    return makeTuple(makeRequest());
  }

  @Override
  public Tuple2<EventSchema, CustomTransformOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                                                                SpDataStream stream2) {
    return makeTuple(makeRequest());
  }

  private EventSchema makeRequest() {
    try {
      String httpRequestBody = JacksonSerializer.getObjectMapper().writeValueAsString(dataProcessorInvocation);
      String endpointUrl = new ExtensionsServiceEndpointGenerator(dataProcessorInvocation.getAppId(),
          SpServiceUrlProvider.DATA_PROCESSOR).getEndpointResourceUrl();
      Response httpResp = Request.Post(endpointUrl + "/output").bodyString(httpRequestBody,
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

    return JacksonSerializer
        .getObjectMapper()
        .readValue(resp, EventSchema.class);
  }
}
