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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.SuccessMessage;

import java.util.List;
import java.util.Optional;

public class AdapterApi extends AbstractTypedClientApi<AdapterDescription>
    implements IAdapterApi {

  public AdapterApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, AdapterDescription.class);
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("connect")
        .addToPath("master")
        .addToPath("adapters");
  }

  @Override
  public Optional<AdapterDescription> get(String elementId) {
    return getSingle(getBaseResourcePath().addToPath(elementId));
  }

  @Override
  public List<AdapterDescription> all() {
    return getAll(getBaseResourcePath());
  }

  @Override
  public void create(AdapterDescription element) {
    post(getBaseResourcePath(), element);
  }

  @Override
  public void delete(String id) {
    delete(getBaseResourcePath().addToPath(id), SuccessMessage.class);
  }

  @Override
  public void update(AdapterDescription element) {
    put(getBaseResourcePath().addToPath(element.getElementId()), element);
  }

  @Override
  public SuccessMessage start(String elementId) {
    return post(getBaseResourcePath().addToPath(elementId).addToPath("start"), SuccessMessage.class);
  }

  @Override
  public SuccessMessage start(AdapterDescription adapter) {
    return start(adapter.getElementId());
  }

  @Override
  public SuccessMessage stop(AdapterDescription adapter) {
    return stop(adapter.getElementId());
  }

  @Override
  public SuccessMessage stop(String elementId) {
    return post(getBaseResourcePath().addToPath(elementId).addToPath("stop"), SuccessMessage.class);
  }
}
