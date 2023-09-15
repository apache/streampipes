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

import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class ExtensionServiceExecutions {

  public static Request extServiceGetRequest(String url) {
    return Request
        .Get(url)
        .addHeader("Authorization", AuthTokenUtils.getAuthTokenForUser(getServiceAdminSid()))
        .addHeader("Accept", "application/json")
        .connectTimeout(10000)
        .socketTimeout(10000);
  }


  private static String getServiceAdminSid() {
    return new SpResourceManager().manageUsers().getServiceAdmin().getPrincipalId();
  }

  public static Request extServicePostRequest(String url,
                                              String payload) {
    return authenticatedPostRequest(url, AuthTokenUtils.getAuthTokenForCurrentUser(), payload);
  }

  public static Request extServicePostRequest(String url,
                                             String elementId,
                                             String payload) {
    return authenticatedPostRequest(
        url,
        AuthTokenUtils.getAuthToken(elementId),
        payload
    );
  }

  private static Request authenticatedPostRequest(String url,
                                                  String token,
                                                  String payload) {
    return Request.Post(url)
        .addHeader("Authorization", token)
        .bodyString(payload, ContentType.APPLICATION_JSON)
        .connectTimeout(1000)
        .socketTimeout(100000);
  }
}
