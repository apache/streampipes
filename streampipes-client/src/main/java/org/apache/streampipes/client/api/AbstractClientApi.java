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
package org.apache.streampipes.client.api;

import org.apache.streampipes.client.http.DeleteRequest;
import org.apache.streampipes.client.http.GetRequest;
import org.apache.streampipes.client.http.PostRequestWithPayloadResponse;
import org.apache.streampipes.client.http.PostRequestWithoutPayload;
import org.apache.streampipes.client.http.PostRequestWithoutPayloadResponse;
import org.apache.streampipes.client.http.PutRequest;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.ObjectSerializer;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.util.Optional;

public class AbstractClientApi {

  protected StreamPipesClientConfig clientConfig;

  public AbstractClientApi(StreamPipesClientConfig clientConfig) {
    this.clientConfig = clientConfig;
  }

  protected <T> T post(StreamPipesApiPath apiPath, Class<T> responseClass) {
    ObjectSerializer<Void, T> serializer = new ObjectSerializer<>();
    return new PostRequestWithPayloadResponse<>(clientConfig, apiPath, serializer, responseClass).executeRequest();
  }

  protected void post(StreamPipesApiPath apiPath) {
    ObjectSerializer<Void, Void> serializer = new ObjectSerializer<>();
    new PostRequestWithoutPayload<>(clientConfig, apiPath, serializer).executeRequest();
  }

  protected <T> void post(StreamPipesApiPath apiPath, T object) {
    ObjectSerializer<T, Void> serializer = new ObjectSerializer<>();
    new PostRequestWithoutPayloadResponse<>(clientConfig, apiPath, serializer, object).executeRequest();
  }

  protected <T> void put(StreamPipesApiPath apiPath, T object) {
    ObjectSerializer<T, Void> serializer = new ObjectSerializer<>();
    new PutRequest<>(clientConfig, apiPath, serializer, object).executeRequest();
  }

  protected <T> T delete(StreamPipesApiPath apiPath, Class<T> responseClass) {
    Serializer<Void, T, T> serializer = new ObjectSerializer<>();
    return new DeleteRequest<>(clientConfig, apiPath, responseClass, serializer).executeRequest();
  }

  protected <K, V> V delete(StreamPipesApiPath apiPath,
                            K object,
                            Class<V> responseClass) {
    Serializer<K, V, V> serializer = new ObjectSerializer<>();
    return new DeleteRequest<>(clientConfig, apiPath, responseClass, serializer, object).executeRequest();
  }

  protected <K, V> V post(StreamPipesApiPath apiPath, K object, Class<V> responseClass) {
    ObjectSerializer<K, V> serializer = new ObjectSerializer<>();
    return new PostRequestWithPayloadResponse<>(clientConfig, apiPath, serializer, object, responseClass)
        .executeRequest();
  }

  protected <T> T getSingle(StreamPipesApiPath apiPath, Class<T> targetClass) throws SpRuntimeException {
    ObjectSerializer<Void, T> serializer = new ObjectSerializer<>();
    return new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest();
  }

  protected <T> Optional<T> getSingleOpt(StreamPipesApiPath apiPath,
                                         Class<T> targetClass) throws SpRuntimeException {
    try {
      ObjectSerializer<Void, T> serializer = new ObjectSerializer<>();
      return Optional.of(new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest());
    } catch (SpHttpErrorStatusCode e) {
      if (e.getHttpStatusCode() == 404) {
        return Optional.empty();
      } else {
        throw e;
      }
    }
  }
}
