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

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;

import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class GetRequest<K, V> extends HttpRequest<Void, K, V> {

  private Class<K> targetClass;

  public GetRequest(StreamPipesClientConfig clientConfig,
                    StreamPipesApiPath apiPath,
                    Class<K> targetClass,
                    Serializer<Void, K, V> serializer) {
    super(clientConfig, apiPath, serializer);
    this.targetClass = targetClass;
  }

  @Override
  protected Request makeRequest(Serializer<Void, K, V> serializer) {
    return Request
        .Get(makeUrl())
        .setHeaders(standardJsonHeaders());
  }

  @Override
  protected V afterRequest(Serializer<Void, K, V> serializer, HttpEntity entity) throws IOException {
    return serializer.deserialize(entityAsString(entity), targetClass);
  }
}
