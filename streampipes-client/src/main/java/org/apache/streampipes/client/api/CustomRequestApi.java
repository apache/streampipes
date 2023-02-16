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

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;

import java.util.Map;

public class CustomRequestApi extends AbstractClientApi {

  public CustomRequestApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig);
  }

  public <T> void sendPost(String apiPath, T payload) {
    post(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), payload);
  }

  public <T> T sendGet(String apiPath, Class<T> responseClass) {
    return getSingle(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), responseClass);
  }

  public <T> T sendGet(String apiPath, Map<String, String> queryParameters, Class<T> responseClass) {
    return getSingle(
        StreamPipesApiPath.fromStreamPipesBasePath(apiPath)
            .withQueryParameters(queryParameters),
        responseClass);
  }

}
