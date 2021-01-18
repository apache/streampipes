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

import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;

import java.io.IOException;

public class DeleteRequest<DSO, DT> extends HttpRequest<Void, DSO, DT> {

  private Class<DSO> responseClass;

  public DeleteRequest(StreamPipesClientConfig clientConfig,
                       StreamPipesApiPath apiPath,
                       Class<DSO> responseClass,
                       Serializer<Void, DSO, DT> serializer) {
    super(clientConfig, apiPath, serializer);
    this.responseClass = responseClass;
  }

  @Override
  protected Request makeRequest(Serializer<Void, DSO, DT> serializer) {
    return Request
            .Delete(makeUrl())
            .setHeaders(standardHeaders());
  }

  @Override
  protected DT afterRequest(Serializer<Void, DSO, DT> serializer, HttpEntity entity) throws IOException {
    return serializer.deserialize(entityAsString(entity), responseClass);
  }
}
