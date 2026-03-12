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

package org.apache.streampipes.manager.execution;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpExtensionServiceRequestManager implements ExtensionServiceRequestManager {

  @Override
  public ExtensionServiceOperationResult requestContainerProvidedOptions(ExtensionServiceRequestTarget target,
                                                                         String payload) throws IOException {
    return post(
        makeUrl(target),
        AuthTokenUtils.getAuthTokenForCurrentUser(),
        payload
    );
  }

  @Override
  public ExtensionServiceOperationResult requestMigration(ExtensionServiceRequestTarget target,
                                                          String payload) throws IOException {
    return post(makeUrl(target), AuthTokenUtils.getAuthTokenForCurrentUser(), payload);
  }

  @Override
  public ExtensionServiceOperationResult requestDescriptionUpdate(ExtensionServiceRequestTarget target) throws IOException {
    return get(makeUrl(target), AuthTokenUtils.getAuthTokenForUser(getServiceAdminSid()));
  }

  @Override
  public ExtensionServiceOperationResult requestExtensionDescription(ExtensionServiceRequestTarget target) throws IOException {
    return get(makeUrl(target), AuthTokenUtils.getAuthTokenForUser(getServiceAdminSid()));
  }

  @Override
  public ExtensionServiceOperationResult requestFunctionStop(ExtensionServiceRequestTarget target) throws IOException {
    return post(makeUrl(target), AuthTokenUtils.getAuthTokenForUser(getServiceAdminSid()), null);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterStateChange(ExtensionServiceRequestTarget target,
                                                                   String elementId,
                                                                   String payload) throws IOException {
    return post(makeUrl(target), AuthTokenUtils.getAuthToken(elementId), payload);
  }

  @Override
  public ExtensionServiceOperationResult requestRuntimeOptions(ExtensionServiceRequestTarget target,
                                                               String payload) throws IOException {
    return post(makeUrl(target), AuthTokenUtils.getAuthTokenForCurrentUser(), payload);
  }

  @Override
  public ExtensionServiceOperationResult requestSampleData(ExtensionServiceRequestTarget target,
                                                           String payload) throws IOException {
    return post(makeUrl(target), AuthTokenUtils.getAuthTokenForCurrentUser(), payload);
  }

  @Override
  public ExtensionServiceOperationResult requestExtensionInstanceHealth(ExtensionServiceRequestTarget target) throws IOException {
    return get(makeUrl(target), AuthTokenUtils.getAuthTokenForUser(getServiceAdminSid()));
  }

  @Override
  public ExtensionServiceOperationResult requestServiceHealth(ExtensionServiceRequestTarget target) throws IOException {
    return get(makeUrl(target), AuthTokenUtils.getAuthTokenForUser(getServiceAdminSid()));
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementInvocation(ExtensionServiceRequestTarget target,
                                                                          String pipelineId,
                                                                          String payload) throws IOException {
    return post(makeUrl(target), AuthTokenUtils.getAuthToken(pipelineId), payload);
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementDetach(ExtensionServiceRequestTarget target,
                                                                      String pipelineId) throws IOException {
    return delete(makeUrl(target), AuthTokenUtils.getAuthToken(pipelineId));
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementAssets(ExtensionServiceRequestTarget target) throws IOException {
    return getWithoutAcceptHeader(makeUrl(target), null);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterAssets(ExtensionServiceRequestTarget target) throws IOException {
    return getWithoutAcceptHeader(makeUrl(target), null);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterIconAsset(ExtensionServiceRequestTarget target) throws IOException {
    return getWithoutAcceptHeader(makeUrl(target), null);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterDocumentationAsset(ExtensionServiceRequestTarget target) throws IOException {
    return getWithoutAcceptHeader(makeUrl(target), null);
  }

  @Override
  public ExtensionServiceOperationResult requestOutputSchema(ExtensionServiceRequestTarget target,
                                                             String payload) throws IOException {
    return post(makeUrl(target), null, payload);
  }

  private ExtensionServiceOperationResult get(String url,
                                              String token) throws IOException {
    var request = Request
        .Get(url)
        .addHeader("Accept", "application/json")
        .connectTimeout(10000)
        .socketTimeout(10000);

    var response = addAuthorizationHeader(request, token)
        .execute()
        .returnResponse();

    return new ExtensionServiceOperationResult(
        response.getStatusLine().getStatusCode(),
        response.getEntity() == null ? null : EntityUtils.toByteArray(response.getEntity())
    );
  }

  private ExtensionServiceOperationResult getWithoutAcceptHeader(String url,
                                                                 String token) throws IOException {
    var request = Request
        .Get(url)
        .connectTimeout(10000)
        .socketTimeout(10000);

    var response = addAuthorizationHeader(request, token)
        .execute()
        .returnResponse();

    return new ExtensionServiceOperationResult(
        response.getStatusLine().getStatusCode(),
        response.getEntity() == null ? null : EntityUtils.toByteArray(response.getEntity())
    );
  }

  private ExtensionServiceOperationResult post(String url,
                                               String token,
                                               String payload) throws IOException {
    var request = Request
        .Post(url)
        .addHeader("Accept", "application/json");

    if (payload != null) {
      request = request.bodyString(payload, ContentType.APPLICATION_JSON);
    }

    var response = addAuthorizationHeader(request, token)
        .connectTimeout(payload == null ? 10000 : 1000)
        .socketTimeout(payload == null ? 10000 : 100000)
        .execute()
        .returnResponse();

    return new ExtensionServiceOperationResult(
        response.getStatusLine().getStatusCode(),
        response.getEntity() == null ? null : EntityUtils.toByteArray(response.getEntity())
    );
  }

  private ExtensionServiceOperationResult delete(String url,
                                                 String token) throws IOException {
    var response = addAuthorizationHeader(Request.Delete(url), token)
        .addHeader("Accept", "application/json")
        .connectTimeout(10000)
        .socketTimeout(10000)
        .execute()
        .returnResponse();

    return new ExtensionServiceOperationResult(
        response.getStatusLine().getStatusCode(),
        response.getEntity() == null ? null : EntityUtils.toByteArray(response.getEntity())
    );
  }

  private Request addAuthorizationHeader(Request request, String token) {
    return token == null ? request : request.addHeader("Authorization", token);
  }

  private String getServiceAdminSid() {
    return new SpResourceManager().manageUsers().getServiceAdmin().getPrincipalId();
  }

  private String makeUrl(ExtensionServiceRequestTarget target) {
    return target.params().toUrl(target.baseUrl());
  }
}
