/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.wrapper.declarer;

import org.streampipes.model.Response;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class PipelineElementDeclarer<B extends BindingParams, EPR extends
        PipelineElementRuntime, I
        extends InvocableStreamPipesEntity, EX extends AbstractParameterExtractor<I>> {

  protected EPR epRuntime;
  protected String elementId;

  public Response invokeEPRuntime(I graph) {

    try {
      elementId = graph.getElementId();
      // change to getRuntime(graph, extractor)
      epRuntime = getRuntime(graph, getExtractor(graph));
      epRuntime.bindRuntime();
      return new Response(graph.getElementId(), true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(graph.getElementId(), false, e.getMessage());
    }

  }

  public Response detachRuntime(String pipelineId) {
    try {
      epRuntime.discardRuntime();
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  protected abstract EX getExtractor(I graph);

  public abstract EPR getRuntime(I graph, EX extractor);




}
