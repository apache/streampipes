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

import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.client.http.header.Headers;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class HttpRequest<K, V, T> {

  private final StreamPipesClientConfig clientConfig;
  private final ClientConnectionUrlResolver connectionConfig;
  private final StreamPipesApiPath apiPath;
  private final ObjectMapper objectMapper;
  private final Serializer<K, V, T> serializer;

  public HttpRequest(StreamPipesClientConfig clientConfig,
                     StreamPipesApiPath apiPath,
                     Serializer<K, V, T> serializer) {
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

  protected String makeUrl() throws SpRuntimeException {
    return makeUrl(true);
  }

  protected String makeUrl(boolean includePath) throws SpRuntimeException {
    String baseUrl = clientConfig.getConnectionConfig().getBaseUrl();
    if (includePath) {
      baseUrl = baseUrl + "/" + apiPath.toString();
    }

    return baseUrl;
  }

  public T executeRequest() {
    Request request = makeRequest(serializer);
    try {
      HttpResponse response = request.execute().returnResponse();
      StatusLine status = response.getStatusLine();
      if (status.getStatusCode() == HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_CREATED) {
        return afterRequest(serializer, response.getEntity());
      } else {
        switch (status.getStatusCode()) {
          case HttpStatus.SC_UNAUTHORIZED -> throw new SpHttpErrorStatusCode(
              " 401 - Access to this resource is forbidden - did you provide a poper API key or client secret?",
              401);
          case HttpStatus.SC_NOT_FOUND ->
              throw new SpHttpErrorStatusCode(" 404 - The requested resource could not be found.", 404);
          default -> throw new SpHttpErrorStatusCode(status.getStatusCode() + " - " + status.getReasonPhrase(),
              status.getStatusCode());
        }
      }
    } catch (IOException e) {
      throw new SpRuntimeException(
          "Could not connect to the StreamPipes API - please check that StreamPipes is available", e);
    }
  }

  public void writeToFile(String fileLocation) throws SpRuntimeException {

    String urlString = makeUrl();
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    try {
      URLConnection connection = url.openConnection();

      connection.setRequestProperty("Authorization",
          connectionConfig.getCredentials().makeHeaders().get(0).getElements()[0].getName());

      try {
        ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
        FileOutputStream fileOutputStream = new FileOutputStream(fileLocation);
        fileOutputStream.getChannel()
            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      } catch (IOException e) {
        e.printStackTrace();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected String entityAsString(HttpEntity entity) throws IOException {
    return EntityUtils.toString(entity);
  }

  protected byte[] entityAsByteArray(HttpEntity entity) throws IOException {
    return EntityUtils.toByteArray(entity);
  }

  protected abstract Request makeRequest(Serializer<K, V, T> serializer);

  protected abstract T afterRequest(Serializer<K, V, T> serializer, HttpEntity entity) throws IOException;

}
