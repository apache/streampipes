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
import org.apache.streampipes.model.shared.annotation.ExposedToScripts;

import java.util.List;
import java.util.Map;

public class CustomRequestApi extends AbstractClientApi implements ICustomRequestApi {

  public CustomRequestApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig);
  }

  @Override
  @ExposedToScripts
  public <T> void sendPost(String apiPath, T payload) {
    post(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), payload);
  }

  @Override
  @ExposedToScripts
  public Object sendPostJson(String apiPath, Object payload) {
    return post(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), payload, Object.class);
  }

  @Override
  @ExposedToScripts
  public <T> T sendGet(String apiPath, Class<T> responseClass) {
    return getSingle(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), responseClass);
  }

  @Override
  @ExposedToScripts
  public <T> T sendGet(String apiPath, Map<String, String> queryParameters, Class<T> responseClass) {
    return getSingle(
        StreamPipesApiPath.fromStreamPipesBasePath(apiPath)
            .withQueryParameters(queryParameters),
        responseClass);
  }

  @Override
  @ExposedToScripts
  public Object sendGetJson(String apiPath) {
    return getSingle(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), Object.class);
  }

  @Override
  @ExposedToScripts
  public Object sendGetJson(String apiPath, Map<String, String> queryParameters) {
    return getSingle(
        StreamPipesApiPath.fromStreamPipesBasePath(apiPath)
            .withQueryParameters(queryParameters),
        Object.class);
  }

  @Override
  @ExposedToScripts
  public <T> void sendPut(String apiPath, T payload) {
    put(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), payload);
  }

  @Override
  @ExposedToScripts
  public Object sendPutJson(String apiPath, Object payload) {
    return put(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), payload, Object.class);
  }

  @Override
  @ExposedToScripts
  public void sendDelete(String apiPath) {
    delete(StreamPipesApiPath.fromStreamPipesBasePath(apiPath));
  }

  @Override
  @ExposedToScripts
  public Object sendDeleteJson(String apiPath) {
    return delete(StreamPipesApiPath.fromStreamPipesBasePath(apiPath), Object.class);
  }

  @Override
  @ExposedToScripts
  public <T> List<T> getList(String apiPath, Class<T> responseClass) {
    return getList(
        StreamPipesApiPath.fromStreamPipesBasePath(apiPath), responseClass
    );
  }

}
