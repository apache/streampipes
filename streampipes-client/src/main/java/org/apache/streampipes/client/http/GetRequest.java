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

import org.apache.http.client.fluent.Request;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.io.IOException;
import java.util.List;

public class GetRequest<T> extends HttpRequest {

  private Class<T> targetClass;

  public GetRequest(StreamPipesClientConfig clientConfig,
                    StreamPipesApiPath apiPath,
                    Class<T> targetClass) {
    super(clientConfig, apiPath);
    this.targetClass = targetClass;
  }

  public T receiveSingle() throws SpRuntimeException {
    return deserialize(executeRequest(), targetClass);
  }

  public List<T> receiveList() throws SpRuntimeException {
    return deserializeList(executeRequest(), targetClass);
  }

  private String executeRequest() throws SpRuntimeException {
    try {
      return Request
              .Get(makeUrl())
              .setHeaders(standardHeaders())
              .execute()
              .returnContent()
              .asString();
    } catch (IOException e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }
}
