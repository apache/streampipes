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

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class EventSinkDeclarer<B extends EventSinkBindingParams, ES extends
        PipelineElementRuntime>
        extends PipelineElementDeclarer<B, ES, DataSinkInvocation,
        DataSinkParameterExtractor> implements SemanticEventConsumerDeclarer {

  @Override
  protected DataSinkParameterExtractor getExtractor(DataSinkInvocation graph) {
    return DataSinkParameterExtractor.from(graph);
  }

  @Override
  public Response invokeRuntime(DataSinkInvocation graph) {
    return invokeEPRuntime(graph);
  }

}
