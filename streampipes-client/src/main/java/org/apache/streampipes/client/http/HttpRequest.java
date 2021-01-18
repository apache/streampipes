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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.apache.streampipes.client.http.header.Headers;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class HttpRequest<SO, DSO, DT> {

  private StreamPipesClientConfig clientConfig;
  private StreamPipesApiPath apiPath;
  private ObjectMapper objectMapper;
  private Serializer<SO, DSO, DT> serializer;

  public HttpRequest(StreamPipesClientConfig clientConfig,
                     StreamPipesApiPath apiPath,
                     Serializer<SO, DSO, DT> serializer) {
    this.clientConfig = clientConfig;
    this.objectMapper = clientConfig.getSerializer();
    this.apiPath = apiPath;
    this.serializer = serializer;
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

  protected String makeUrl() {
    return makeProtocol() + clientConfig.getStreamPipesHost()
            + ":"
            + clientConfig.getStreamPipesPort()
            + "/"
            + apiPath.toString();
  }

  public DT executeRequest() throws SpRuntimeException {
    Request request = makeRequest(serializer);
    try {
      HttpResponse response = request.execute().returnResponse();
      StatusLine status = response.getStatusLine();
      if (status.getStatusCode() == HttpStatus.SC_OK) {
        return afterRequest(serializer, response.getEntity());
      } else {
        throw new SpRuntimeException("Status: " + status.getStatusCode());
      }
    } catch (IOException e) {
      throw new SpRuntimeException(e.getCause());
    }
  }

  protected String entityAsString(HttpEntity entity) throws IOException {
    return EntityUtils.toString(entity);
  }

  protected abstract Request makeRequest(Serializer<SO, DSO, DT> serializer);

  protected abstract DT afterRequest(Serializer<SO, DSO, DT> serializer, HttpEntity entity) throws IOException;

  private String makeProtocol() {
    return clientConfig.isHttpsDisabled() ? "http://" : "https://";
  }


}
