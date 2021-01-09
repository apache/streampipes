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
package org.apache.streampipes.client.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.http.Header;
import org.apache.streampipes.client.http.header.Headers;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpRequest {

  private StreamPipesClientConfig clientConfig;
  private StreamPipesApiPath apiPath;
  private ObjectMapper objectMapper;

  public HttpRequest(StreamPipesClientConfig clientConfig, StreamPipesApiPath apiPath) {
    this.clientConfig = clientConfig;
    this.objectMapper = clientConfig.getSerializer();
    this.apiPath = apiPath;
  }

  protected Header[] standardHeaders() {
    List<Header> headers = new ArrayList<>();
    headers.add(Headers.auth(clientConfig.getCredentials()));
    headers.add(Headers.acceptJson());
    return headers.toArray(new Header[0]);
  }

  protected Header[] standardPostHeaders() {
    List<Header> headers = new ArrayList<>(Arrays.asList(standardHeaders()));
    headers.add(Headers.contentTypeJson());
    return headers.toArray(new Header[0]);
  }

  protected <T> String serialize(T element) {
    try {
      return objectMapper.writeValueAsString(element);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException(e.getCause());
    }
  }

  protected <T> T deserialize(String json, Class<T> targetClass) throws SpRuntimeException {
    try {
      return objectMapper.readValue(json, targetClass);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException(e.getCause());
    }
  }

  protected <T> List<T> deserializeList(String json, Class<T> targetClass) throws SpRuntimeException {
    CollectionType listType = objectMapper.getTypeFactory()
            .constructCollectionType(List.class, targetClass);
    try {
      return objectMapper.readValue(json, listType);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException(e.getCause());
    }
  }

  protected String makeUrl() {
    return makeProtocol() + clientConfig.getStreamPipesHost()
            + ":"
            + clientConfig.getStreamPipesPort()
            + "/"
            + apiPath.toString();
  }

  private String makeProtocol() {
    return clientConfig.isHttpsDisabled() ? "http://" : "https://";
  }


}
