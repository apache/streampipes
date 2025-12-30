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

package org.apache.streampipes.storage.couchdb.utils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

/**
 * Factory class for creating authenticated HTTP requests to CouchDB.
 * <p>
 * This utility centralizes the creation of {@link Request} instances with a
 * preconfigured Basic Authorization header and connection/socket timeouts.
 * It uses {@link CouchDbAuthUtils} to obtain the credentials for CouchDB.
 */

public final class CouchDbRequestFactory {

  private final CouchDbAuthUtils authUtils;

  public CouchDbRequestFactory(CouchDbAuthUtils authUtils) {
    this.authUtils = authUtils;
  }

  public Request get(String route) {
    return append(Request.Get(route));
  }

  public Request post(String route, String payload) {
    return append(Request.Post(route)
        .bodyString(payload, ContentType.APPLICATION_JSON));
  }

  public Request put(String route, String payload) {
    return append(Request.Put(route)
        .bodyString(payload, ContentType.APPLICATION_JSON));
  }

  public Request delete(String route) {
    return append(Request.Delete(route));
  }

  private Request append(Request req) {
    req.setHeader(HttpHeaders.AUTHORIZATION,
            "Basic " + authUtils.getBasicAuthHeaderValue())
        .connectTimeout(1000)
        .socketTimeout(100000);
    return req;
  }
}
