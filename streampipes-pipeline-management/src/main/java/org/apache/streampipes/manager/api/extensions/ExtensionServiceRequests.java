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

package org.apache.streampipes.manager.api.extensions;

import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.resource.management.SpResourceManager;

public final class ExtensionServiceRequests {

  private ExtensionServiceRequests() {
  }

  public static ExtensionServiceRequest get(ExtensionServiceRequestTarget target, String authToken) {
    return ExtensionServiceRequest.get(target, authToken);
  }

  public static ExtensionServiceRequest get(ExtensionServiceRequestTarget target,
                                            String authToken,
                                            boolean acceptJsonResponse) {
    return ExtensionServiceRequest.get(target, authToken, acceptJsonResponse);
  }

  public static ExtensionServiceRequest post(ExtensionServiceRequestTarget target,
                                             String payload,
                                             String authToken) {
    return ExtensionServiceRequest.post(target, payload, authToken);
  }

  public static ExtensionServiceRequest delete(ExtensionServiceRequestTarget target, String authToken) {
    return ExtensionServiceRequest.delete(target, authToken);
  }

  public static ExtensionServiceRequest containerProvidedOptions(ExtensionServiceRequestTarget target,
                                                                 String payload,
                                                                 String authToken) {
    return post(target, payload, authToken);
  }

  public static ExtensionServiceRequest migration(ExtensionServiceRequestTarget target,
                                                  String payload,
                                                  SpResourceManager resourceManager) {
    return post(
        target,
        payload,
        AuthTokenUtils.getAuthTokenForCurrentUser(resourceManager.getCoreConfigurationStorage())
    );
  }

  public static ExtensionServiceRequest descriptionUpdate(ExtensionServiceRequestTarget target,
                                                          SpResourceManager resourceManager) {
    return get(target, serviceAdminToken(resourceManager));
  }

  public static ExtensionServiceRequest extensionDescription(ExtensionServiceRequestTarget target,
                                                             SpResourceManager resourceManager) {
    return get(target, serviceAdminToken(resourceManager));
  }

  public static ExtensionServiceRequest functionStop(ExtensionServiceRequestTarget target,
                                                     SpResourceManager resourceManager) {
    return post(target, null, serviceAdminToken(resourceManager));
  }

  public static ExtensionServiceRequest adapterStateChange(ExtensionServiceRequestTarget target,
                                                           String elementId,
                                                           String payload,
                                                           SpResourceManager resourceManager) {
    return post(target, payload, AuthTokenUtils.getAuthToken(elementId, resourceManager));
  }

  public static ExtensionServiceRequest runtimeOptions(ExtensionServiceRequestTarget target,
                                                       String payload,
                                                       SpResourceManager resourceManager) {
    return post(
        target,
        payload,
        AuthTokenUtils.getAuthTokenForCurrentUser(resourceManager.getCoreConfigurationStorage())
    );
  }

  public static ExtensionServiceRequest sampleData(ExtensionServiceRequestTarget target,
                                                   String payload,
                                                   SpResourceManager resourceManager) {
    return post(
        target,
        payload,
        AuthTokenUtils.getAuthTokenForCurrentUser(resourceManager.getCoreConfigurationStorage())
    );
  }

  public static ExtensionServiceRequest extensionInstanceHealth(ExtensionServiceRequestTarget target,
                                                                SpResourceManager resourceManager) {
    return get(target, serviceAdminToken(resourceManager));
  }

  public static ExtensionServiceRequest serviceHealth(ExtensionServiceRequestTarget target,
                                                      SpResourceManager resourceManager) {
    return get(target, serviceAdminToken(resourceManager));
  }

  public static ExtensionServiceRequest serviceLoad(ExtensionServiceRequestTarget target,
                                                    SpResourceManager resourceManager) {
    return get(target, serviceAdminToken(resourceManager));
  }

  public static ExtensionServiceRequest pipelineElementInvocation(ExtensionServiceRequestTarget target,
                                                                  String payload,
                                                                  String authToken) {
    return post(target, payload, authToken);
  }

  public static ExtensionServiceRequest pipelineElementDetach(ExtensionServiceRequestTarget target,
                                                              String pipelineId,
                                                              SpResourceManager resourceManager) {
    return delete(target, AuthTokenUtils.getAuthToken(pipelineId, resourceManager));
  }

  public static ExtensionServiceRequest pipelineElementAssets(ExtensionServiceRequestTarget target) {
    return get(target, null, false);
  }

  public static ExtensionServiceRequest pipelineElementIconAsset(ExtensionServiceRequestTarget target) {
    return get(target, null, false);
  }

  public static ExtensionServiceRequest adapterAssets(ExtensionServiceRequestTarget target) {
    return get(target, null, false);
  }

  public static ExtensionServiceRequest adapterIconAsset(ExtensionServiceRequestTarget target) {
    return get(target, null, false);
  }

  public static ExtensionServiceRequest adapterDocumentationAsset(ExtensionServiceRequestTarget target) {
    return get(target, null, false);
  }

  public static ExtensionServiceRequest outputSchema(ExtensionServiceRequestTarget target, String payload) {
    return post(target, payload, null);
  }

  private static String serviceAdminToken(SpResourceManager resourceManager) {
    return AuthTokenUtils.getAuthTokenForUser(
        resourceManager.manageUsers().getServiceAdmin().getPrincipalId(),
        resourceManager);
  }
}
