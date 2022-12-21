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

import java.io.IOException;

public class PostRequestWithoutPayloadResponse<K, V> extends PostRequest<K, V, Void> {

  public PostRequestWithoutPayloadResponse(StreamPipesClientConfig clientConfig,
                                           StreamPipesApiPath apiPath,
                                           Serializer<K, V, Void> serializer,
                                           K body) {
    super(clientConfig, apiPath, serializer, body);
  }

  public PostRequestWithoutPayloadResponse(StreamPipesClientConfig clientConfig,
                                           StreamPipesApiPath apiPath,
                                           Serializer<K, V, Void> serializer) {
    super(clientConfig, apiPath, serializer);
  }


  @Override
  protected Void afterRequest(Serializer<K, V, Void> serializer, HttpEntity entity) throws IOException {
    return null;
  }
}
