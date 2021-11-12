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
import org.apache.streampipes.client.model.ClientConnectionUrlResolver;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class HttpRequest<SO, DSO, DT> {

  private final StreamPipesClientConfig clientConfig;
  private final ClientConnectionUrlResolver connectionConfig;
  private final StreamPipesApiPath apiPath;
  private final ObjectMapper objectMapper;
  private final Serializer<SO, DSO, DT> serializer;

  public HttpRequest(StreamPipesClientConfig clientConfig,
                     StreamPipesApiPath apiPath,
                     Serializer<SO, DSO, DT> serializer) {
    this.clientConfig = clientConfig;
    this.connectionConfig = clientConfig.getConnectionConfig();
    this.objectMapper = clientConfig.getSerializer();
    this.apiPath = apiPath;
    this.serializer = serializer;
  }

  protected Header[] standardJsonHeaders() {
    List<Header> headers = new ArrayList<>(connectionConfig.getCredentials().makeHeaders());
    headers.add(Headers.acceptJson());
    return headers.toArray(new Header[0]);
  }

  protected Header[] standardHeaders() {
    List<Header> headers = new ArrayList<>(connectionConfig.getCredentials().makeHeaders());
    return headers.toArray(new Header[0]);
  }

  protected Header[] standardPostHeaders() {
    List<Header> headers = new ArrayList<>(Arrays.asList(standardJsonHeaders()));
    headers.add(Headers.contentTypeJson());
    return headers.toArray(new Header[0]);
  }

  protected String makeUrl() {
    return makeUrl(true);
  }

  protected String makeUrl(boolean includePath) {
    String baseUrl = clientConfig.getConnectionConfig().getBaseUrl();
    if (includePath) {
      baseUrl = baseUrl + "/" + apiPath.toString();
    }

    return baseUrl;
  }

  public DT executeRequest() throws SpRuntimeException {
    Request request = makeRequest(serializer);
    try {
      HttpResponse response = request.execute().returnResponse();
      StatusLine status = response.getStatusLine();
      if (status.getStatusCode() == HttpStatus.SC_OK) {
        return afterRequest(serializer, response.getEntity());
      } else {
        if (status.getStatusCode() == 401) {
          throw new SpRuntimeException(" 401 - Access to this resource is forbidden - did you provide a poper API key or client secret?");
        } else {
          throw new SpRuntimeException(status.getStatusCode() + " - " + status.getReasonPhrase());
        }
      }
    } catch (NoHttpResponseException e) {
      throw new SpRuntimeException("Could not connect to the StreamPipes API - please check that StreamPipes is available at " + makeUrl(false));
    } catch (IOException e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  protected String entityAsString(HttpEntity entity) throws IOException {
    return EntityUtils.toString(entity);
  }

  protected byte[] entityAsByteArray(HttpEntity entity) throws IOException {
    return EntityUtils.toByteArray(entity);
  }

  protected abstract Request makeRequest(Serializer<SO, DSO, DT> serializer);

  protected abstract DT afterRequest(Serializer<SO, DSO, DT> serializer, HttpEntity entity) throws IOException;

}
