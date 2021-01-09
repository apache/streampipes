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
package org.apache.streampipes.client.http.header;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.streampipes.client.StreamPipesCredentials;

public class Headers {

  private static final String AUTHORIZATION = "Authorization";
  private static final String ACCEPT = "Accept";
  private static final String CONTENT_TYPE = "Content-type";
  private static final String BEARER = "Bearer ";
  private static final String APPLICATION_JSON_TYPE = "application/json";

  public static Header auth(StreamPipesCredentials credentials) {
    return makeHeader(AUTHORIZATION, BEARER + credentials.getApiKey());
  }

  public static Header acceptJson() {
    return makeHeader(ACCEPT, APPLICATION_JSON_TYPE);
  }

  private static Header makeHeader(String name, String value) {
    return new BasicHeader(name, value);
  }

  public static Header contentTypeJson() {
    return makeHeader(CONTENT_TYPE, APPLICATION_JSON_TYPE);
  }
}
