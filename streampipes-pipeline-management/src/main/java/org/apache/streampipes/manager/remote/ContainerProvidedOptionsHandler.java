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
package org.apache.streampipes.manager.remote;

import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.runtime.ContainerProvidedOptionsParameterRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.serializers.json.GsonSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerProvidedOptionsHandler {


  public List<Option> fetchRemoteOptions(ContainerProvidedOptionsParameterRequest parameterRequest) {

    RuntimeOptionsRequest request = new RuntimeOptionsRequest();
    request.setRequestId(parameterRequest.getRuntimeResolvableInternalId());
    request.setInputStreams(parameterRequest.getInputStreams());
    request.setStaticProperties(parameterRequest.getStaticProperties());
    request.setAppId(parameterRequest.getAppId());
//    Optional<RuntimeResolvableSelectionStaticProperty> runtimeResolvableOpt = findProperty
//            (parameterRequest.getStaticProperties(), parameterRequest.getRuntimeResolvableInternalId());
//
//    if (runtimeResolvableOpt.isPresent()) {
//      RuntimeResolvableSelectionStaticProperty rsp = runtimeResolvableOpt.get();
//      RuntimeOptionsRequest request = new RuntimeOptionsRequest(rsp.getInternalName());
//
//      if (rsp.getLinkedMappingPropertyId() != null) {
//        Optional<EventProperty> eventPropertyOpt = findEventProperty(parameterRequest.getEventProperties(),
//                parameterRequest.getStaticProperties(), rsp
//                        .getLinkedMappingPropertyId());
//        eventPropertyOpt.ifPresent(request::setMappedEventProperty);
//      }
    String httpRequestBody = GsonSerializer.getGsonWithIds()
            .toJson(request);

    try {
      Response httpResp = Request.Post(parameterRequest.getBelongsTo() + "/configurations").bodyString(httpRequestBody, ContentType.APPLICATION_JSON).execute();
      return handleResponse(httpResp);
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private List<Option> handleResponse(Response httpResp) throws JsonSyntaxException, IOException {
    String resp = httpResp.returnContent().asString();
    RuntimeOptionsResponse response = GsonSerializer
            .getGsonWithIds()
            .fromJson(resp, RuntimeOptionsResponse.class);

    return response
            .getOptions()
            .stream()
            .map(Option::new)
            .collect(Collectors.toList());
  }
}
