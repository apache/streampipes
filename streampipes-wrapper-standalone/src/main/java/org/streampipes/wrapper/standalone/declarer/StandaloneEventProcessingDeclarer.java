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

package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventProcessorRuntime;

public abstract class StandaloneEventProcessingDeclarer<B extends
        EventProcessorBindingParams> extends EventProcessorDeclarer<B, StandaloneEventProcessorRuntime> {

  public abstract ConfiguredEventProcessor<B> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor);

  @Override
  public StandaloneEventProcessorRuntime getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    ConfiguredEventProcessor<B> configuredEngine = onInvocation(graph, extractor);
    StandaloneEventProcessorRuntimeParams<B> runtimeParams = new StandaloneEventProcessorRuntimeParams<>
            (configuredEngine.getEngineSupplier(), configuredEngine.getBindingParams(), false);

    return new StandaloneEventProcessorRuntime(runtimeParams);
  }
}
