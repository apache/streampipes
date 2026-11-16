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

package org.apache.streampipes.couchdb.client.transport;

/**
 * An HTTP request against a CouchDB server.
 *
 * @param method      HTTP method
 * @param path        absolute path including the query string, already percent-encoded
 * @param accept      value of the {@code Accept} header
 * @param contentType content type of the body, {@code null} without body
 * @param body        request body, {@code null} for none
 */
public record CouchRequest(String method,
                           String path,
                           String accept,
                           String contentType,
                           byte[] body) {

  public static final String JSON = "application/json";

  public static CouchRequest get(String path) {
    return new CouchRequest("GET", path, JSON, null, null);
  }

  public static CouchRequest head(String path) {
    return new CouchRequest("HEAD", path, JSON, null, null);
  }

  public static CouchRequest delete(String path) {
    return new CouchRequest("DELETE", path, JSON, null, null);
  }

  public static CouchRequest put(String path, byte[] body) {
    return new CouchRequest("PUT", path, JSON, JSON, body);
  }

  public static CouchRequest post(String path, byte[] body) {
    return new CouchRequest("POST", path, JSON, JSON, body);
  }

  public CouchRequest withAccept(String accept) {
    return new CouchRequest(method, path, accept, contentType, body);
  }

  public CouchRequest withBody(String contentType, byte[] body) {
    return new CouchRequest(method, path, accept, contentType, body);
  }
}
