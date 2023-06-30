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
import org.apache.streampipes.model.template.PipelineElementTemplate;

import java.util.List;
import java.util.Optional;

public class PipelineElementTemplateApi extends AbstractTypedClientApi<PipelineElementTemplate>
    implements IPipelineElementTemplateApi {

  public PipelineElementTemplateApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, PipelineElementTemplate.class);
  }

  @Override
  public Optional<PipelineElementTemplate> get(String id) {
    return getSingle(getBaseResourcePath().addToPath(id));
  }

  @Override
  public List<PipelineElementTemplate> all() {
    return getAll(getBaseResourcePath());
  }

  @Override
  public void create(PipelineElementTemplate element) {
    post(getBaseResourcePath(), element);
  }

  @Override
  public void delete(String s) {

  }

  @Override
  public void update(PipelineElementTemplate element) {

  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("pipeline-element-templates");
  }
}
