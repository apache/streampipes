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
package org.streampipes.manager.remote;

import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.streampipes.model.client.runtime.ContainerProvidedOptionsParameterRequest;
import org.streampipes.model.runtime.RuntimeOptionsRequest;
import org.streampipes.model.runtime.RuntimeOptionsResponse;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.RuntimeResolvableSelectionStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.StaticPropertyType;
import org.streampipes.serializers.json.GsonSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContainerProvidedOptionsHandler {


  public List<Option> fetchRemoteOptions(ContainerProvidedOptionsParameterRequest parameterRequest) {

    Optional<RuntimeResolvableSelectionStaticProperty> runtimeResolvableOpt = findProperty
            (parameterRequest.getStaticProperties(), parameterRequest.getRuntimeResolvableInternalId());

    if (runtimeResolvableOpt.isPresent()) {
      RuntimeResolvableSelectionStaticProperty rsp = runtimeResolvableOpt.get();
      RuntimeOptionsRequest request = new RuntimeOptionsRequest(rsp.getInternalName());

      if (rsp.getLinkedMappingPropertyId() != null) {
        Optional<EventProperty> eventPropertyOpt = findEventProperty(parameterRequest.getEventProperties(),
                parameterRequest.getStaticProperties(), rsp
                        .getLinkedMappingPropertyId());
        eventPropertyOpt.ifPresent(request::setMappedEventProperty);
      }
      String httpRequestBody = GsonSerializer.getGsonWithIds()
              .toJson
                      (request);
      try {
        Response httpResp = Request.Post(parameterRequest.getBelongsTo() +"/configurations").bodyString(httpRequestBody, ContentType.APPLICATION_JSON).execute();
        return handleResponse(httpResp);
      } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>();
      }
    } else {
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
            .map(o -> new Option(o.getLabel(), o.getAdditionalPayload()))
            .collect(Collectors.toList());
  }

  private Optional<EventProperty> findEventProperty(List<EventProperty> eventProperties, List<StaticProperty>
          staticProperties, String linkedMappingPropertyId) {

    Optional<MappingPropertyUnary> mappingProperty = staticProperties
            .stream()
            .filter(sp -> sp.getInternalName().equals(linkedMappingPropertyId))
            .map(sp -> (MappingPropertyUnary) sp)
            .findFirst();

    if (mappingProperty.isPresent()) {
      return eventProperties
              .stream()
              .filter(ep -> ep.getElementId().equals(mappingProperty.get().getMapsTo().toString()))
              .findFirst();
    } else {
      return Optional.empty();
    }
  }

  private Optional<RuntimeResolvableSelectionStaticProperty> findProperty(List<StaticProperty> staticProperties, String
          runtimeResolvablePropertyId) {

    return staticProperties.stream()
            .filter(sp -> sp.getInternalName().equals(runtimeResolvablePropertyId))
            .filter(sp -> sp.getStaticPropertyType() == StaticPropertyType.RuntimeResolvableAnyStaticProperty || sp
                    .getStaticPropertyType() == StaticPropertyType.RuntimeResolvableOneOfStaticProperty)
            .map(sp -> (RuntimeResolvableSelectionStaticProperty) sp)
            .findFirst();
  }
}
