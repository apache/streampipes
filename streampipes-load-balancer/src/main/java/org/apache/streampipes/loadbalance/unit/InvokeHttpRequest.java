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

package org.apache.streampipes.loadbalance.unit;

import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class InvokeHttpRequest{

  private static final Logger LOG = LoggerFactory.getLogger(InvokeHttpRequest.class);

  protected Request initRequest(EndpointSelectable pipelineElement,
                             String endpointUrl) throws JsonProcessingException {
    LOG.info("Invoking element: " + endpointUrl);
    return Request
        .Post(endpointUrl)
        .bodyString(toJson(pipelineElement), ContentType.APPLICATION_JSON);
  }

  protected void logError(String endpointUrl,
                       String pipelineElementName,
                       String exceptionMessage) {
    LOG.error("Could not perform invocation request at {} for pipeline element {}: {}",
        endpointUrl, pipelineElementName, exceptionMessage);
  }

  private String toJson(EndpointSelectable pipelineElement) throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().writeValueAsString(pipelineElement);
  }

  public PipelineElementStatus execute(EndpointSelectable pipelineElement,
                                       String endpointUrl,
                                       String pipelineId) {
    try {
      Response httpResp = initRequest(pipelineElement, endpointUrl)
              .addHeader("Authorization", getAuthToken(pipelineId))
              .connectTimeout(10000)
              .execute();
      return handleResponse(httpResp, pipelineElement, endpointUrl);
    } catch (Exception e) {
      logError(endpointUrl, pipelineElement.getName(), e.getMessage());
      return new PipelineElementStatus(endpointUrl, pipelineElement.getName(), false, e.getMessage());
    }
  }

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

  public static String getAuthToken(String resourceId) {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return makeBearerToken(new JwtTokenProvider().createToken(auth));
    } else {
      if (resourceId != null) {
        String ownerSid = getOwnerSid(resourceId);
        return getAuthTokenForUser(ownerSid);
      } else {
        throw new IllegalArgumentException("No authenticated user found to associate with request");
      }
    }
  }

  public static String getAuthTokenForUser(String ownerSid) {
    Principal correspondingUser = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI().getUserById(ownerSid);
    return getAuthTokenForUser(correspondingUser);
  }

  public static String getAuthTokenForUser(Principal principal) {
    return makeBearerToken(new JwtTokenProvider().createToken(principal));
  }

  private static String makeBearerToken(String token) {
    return "Bearer " + token;
  }

  private static String getOwnerSid(String resourceId) {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage().getUserPermissionsForObject(resourceId)
            .stream()
            .findFirst()
            .map(Permission::getOwnerSid)
            .orElseThrow(() -> new IllegalArgumentException("Could not find owner for resource " + resourceId));
  }

}
