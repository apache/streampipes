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
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.ListSerializer;
import org.apache.streampipes.client.serializer.ObjectSerializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.util.List;
import java.util.Optional;

public abstract class AbstractTypedClientApi<T> extends AbstractClientApi {

  private Class<T> targetClass;

  public AbstractTypedClientApi(StreamPipesClientConfig clientConfig, Class<T> targetClass) {
    super(clientConfig);
    this.targetClass = targetClass;
  }

  protected List<T> getAll(StreamPipesApiPath apiPath) throws SpRuntimeException {
    ListSerializer<Void, T> serializer = new ListSerializer<>();
    return new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest();
  }

  protected Optional<T> getSingle(StreamPipesApiPath apiPath) throws SpRuntimeException {
    ObjectSerializer<Void, T> serializer = new ObjectSerializer<>();
    try {
      return Optional.of(new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest());
    } catch (SpHttpErrorStatusCode e) {
      if (e.getHttpStatusCode() == 404) {
        return Optional.empty();
      } else {
        throw e;
      }
    }
  }

  protected abstract StreamPipesApiPath getBaseResourcePath();
}
