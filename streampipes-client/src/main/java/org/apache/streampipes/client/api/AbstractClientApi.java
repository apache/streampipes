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

import org.apache.streampipes.client.http.GetRequest;
import org.apache.streampipes.client.http.PostRequestWithPayloadResponse;
import org.apache.streampipes.client.http.PostRequestWithoutPayloadResponse;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.util.List;

public abstract class AbstractClientApi<T> {

  protected StreamPipesClientConfig clientConfig;
  private Class<T> targetClass;

  public AbstractClientApi(StreamPipesClientConfig clientConfig, Class<T> targetClass) {
    this.clientConfig = clientConfig;
    this.targetClass = targetClass;
  }

  protected List<T> getAll(StreamPipesApiPath apiPath) throws SpRuntimeException {
    return new GetRequest<>(clientConfig, apiPath, targetClass).receiveList();
  }

  protected T getSingle(StreamPipesApiPath apiPath) throws SpRuntimeException {
    return new GetRequest<>(clientConfig, apiPath, targetClass).receiveSingle();
  }

  protected <O> O post(StreamPipesApiPath apiPath, T object, Class<O> responseClass) {
    return new PostRequestWithPayloadResponse<>(clientConfig, apiPath, object, responseClass).postItem();
  }

  protected void post(StreamPipesApiPath apiPath, T object) {
    new PostRequestWithoutPayloadResponse<>(clientConfig, apiPath, object).postItem();
  }

  protected abstract StreamPipesApiPath getBaseResourcePath();
}
