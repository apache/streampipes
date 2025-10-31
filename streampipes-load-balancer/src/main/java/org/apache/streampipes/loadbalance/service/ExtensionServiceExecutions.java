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

package org.apache.streampipes.loadbalance.service;

import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.apache.streampipes.commons.environment.Environments.getEnvironment;
import static org.apache.streampipes.loadbalance.unit.InvokeHttpRequest.getAuthToken;
import static org.apache.streampipes.loadbalance.unit.InvokeHttpRequest.getAuthTokenForUser;

public class ExtensionServiceExecutions {

  public static Request extServiceGetRequest(String url) {
    return Request
        .Get(url)
        .addHeader("Authorization", getAuthTokenForUser(getServiceAdminSid()))
        .addHeader("Accept", "application/json")
        .connectTimeout(10000)
        .socketTimeout(10000);
  }


  private static String getServiceAdminSid() {
    IUserStorage storage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
    var env = getEnvironment();
    return storage.getServiceAccount(
            env.getInitialServiceUser().getValueOrDefault()).getPrincipalId();
  }

  public static Request extServicePostRequest(String url,
                                              String payload) {
    return authenticatedPostRequest(url, getAuthTokenForCurrentUser(), payload);
  }

  public static Request extServicePostRequest(String url,
                                             String elementId,
                                             String payload) {
    return authenticatedPostRequest(
        url,
        getAuthToken(elementId),
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

  public static String getAuthTokenForCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return makeBearerToken(new JwtTokenProvider().createToken(auth));
  }

  private static String makeBearerToken(String token) {
    return "Bearer " + token;
  }
}
