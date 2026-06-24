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
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.couchdb.impl.user.PermissionStorageImpl;
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
                                       String pipelineId,
                                       SpResourceManager resourceManager) {
    try {
      Response httpResp = initRequest(pipelineElement, endpointUrl)
              .addHeader("Authorization", getAuthToken(pipelineId, resourceManager))
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

  public static String getAuthToken(String resourceId,
                                    SpResourceManager resourceManager) {
    var configurationStorage = resourceManager.getCoreConfigurationStorage();
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return makeBearerToken(new JwtTokenProvider(
          configurationStorage,
          resourceManager.manageUsers().getDb(),
          resourceManager.getRoleStorage(),
          resourceManager.getUserGroupStorage()
      ).createToken(auth));
    } else {
      if (resourceId != null) {
        String ownerSid = getOwnerSid(resourceId);
        return getAuthTokenForUser(ownerSid, resourceManager);
      } else {
        throw new IllegalArgumentException("No authenticated user found to associate with request");
      }
    }
  }

  public static String getAuthTokenForUser(String ownerSid,
                                           SpResourceManager resourceManager) {
    Principal correspondingUser = resourceManager.manageUsers().getDb().getUserById(ownerSid);
    return getAuthTokenForUser(correspondingUser, resourceManager);
  }

  public static String getAuthTokenForUser(Principal principal,
                                           SpResourceManager resourceManager) {
    return makeBearerToken(new JwtTokenProvider(
        resourceManager.getCoreConfigurationStorage(),
        resourceManager.manageUsers().getDb(),
        resourceManager.getRoleStorage(),
        resourceManager.getUserGroupStorage()
    ).createToken(principal));
  }

  private static String makeBearerToken(String token) {
    return "Bearer " + token;
  }

  private static String getOwnerSid(String resourceId) {
    return new PermissionStorageImpl("users/permissions").getUserPermissionsForObject(resourceId)
            .stream()
            .findFirst()
            .map(Permission::getOwnerSid)
            .orElseThrow(() -> new IllegalArgumentException("Could not find owner for resource " + resourceId));
  }

}
