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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequest;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpExtensionServiceRequestManager implements ExtensionServiceRequestManager {

  @Override
  public ExtensionServiceOperationResult request(ExtensionServiceRequest request) throws IOException {
    var target = request.target();
    var url = makeUrl(target);

    return switch (request.method()) {
      case GET -> get(url, request.authToken(), request.acceptJsonResponse());
      case POST -> post(url, request.authToken(), request.payload());
      case DELETE -> delete(url, request.authToken());
    };
  }

  private ExtensionServiceOperationResult get(String url,
                                              String token,
                                              boolean acceptJsonResponse) throws IOException {
    var request = Request
        .Get(url)
        .connectTimeout(10000)
        .socketTimeout(10000);

    if (acceptJsonResponse) {
      request = request.addHeader("Accept", "application/json");
    }

    var response = addAuthorizationHeader(request, token)
        .execute()
        .returnResponse();
    return toOperationResult(response);
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
    return toOperationResult(response);
  }

  private ExtensionServiceOperationResult delete(String url,
                                                 String token) throws IOException {
    var response = addAuthorizationHeader(Request.Delete(url), token)
        .addHeader("Accept", "application/json")
        .connectTimeout(10000)
        .socketTimeout(10000)
        .execute()
        .returnResponse();
    return toOperationResult(response);
  }

  private ExtensionServiceOperationResult toOperationResult(org.apache.http.HttpResponse response) throws IOException {
    return new ExtensionServiceOperationResult(
        response.getStatusLine().getStatusCode(),
        response.getEntity() == null ? null : EntityUtils.toByteArray(response.getEntity())
    );
  }

  private Request addAuthorizationHeader(Request request, String token) {
    return token == null ? request : request.addHeader("Authorization", token);
  }
  private String makeUrl(ExtensionServiceRequestTarget target) {
    return target.toPath();
  }
}
