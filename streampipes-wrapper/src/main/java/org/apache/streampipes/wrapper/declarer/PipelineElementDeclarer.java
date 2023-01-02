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

package org.apache.streampipes.wrapper.declarer;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.apache.streampipes.wrapper.params.binding.BindingParams;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class PipelineElementDeclarer<T extends BindingParams, V extends
    PipelineElementRuntime, W
    extends InvocableStreamPipesEntity, X extends AbstractParameterExtractor<W>> {

  protected V epRuntime;
  protected String elementId;

  public Response invokeEPRuntime(W graph, String serviceId) {

    try {
      elementId = graph.getElementId();
      ConfigExtractor configExtractor = makeConfigExtractor(serviceId);
      // TODO add StreamPipes Client support
      StreamPipesClient streamPipesClient = new StreamPipesClientResolver().makeStreamPipesClientInstance();
      epRuntime = getRuntime(graph, getExtractor(graph), configExtractor, streamPipesClient);
      epRuntime.bindRuntime();
      return new Response(graph.getElementId(), true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(graph.getElementId(), false, e.getMessage());
    }
  }

  public Response detachRuntime(String pipelineId,
                                String serviceId) {
    try {
      epRuntime.discardRuntime();
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  protected abstract X getExtractor(W graph);

  public abstract V getRuntime(W graph,
                               X extractor,
                               ConfigExtractor configExtractor,
                               StreamPipesClient streamPipesClient);

  private ConfigExtractor makeConfigExtractor(String serviceId) {
    return ConfigExtractor.from(serviceId);
  }

}
