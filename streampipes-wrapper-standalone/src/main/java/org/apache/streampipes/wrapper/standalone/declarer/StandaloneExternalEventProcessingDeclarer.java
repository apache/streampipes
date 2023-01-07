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
package org.apache.streampipes.wrapper.standalone.declarer;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.apache.streampipes.wrapper.standalone.ConfiguredExternalEventProcessor;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneExternalEventProcessorRuntime;

public abstract class StandaloneExternalEventProcessingDeclarer<T extends
    EventProcessorBindingParams> extends EventProcessorDeclarer<T,
    StandaloneExternalEventProcessorRuntime<T>> {

  public abstract ConfiguredExternalEventProcessor<T> onInvocation(DataProcessorInvocation graph,
                                                                   ProcessingElementParameterExtractor extractor);

  @Override
  public StandaloneExternalEventProcessorRuntime<T> getRuntime(DataProcessorInvocation graph,
                                                               ProcessingElementParameterExtractor extractor,
                                                               ConfigExtractor configExtractor,
                                                               StreamPipesClient streamPipesClient) {
    ConfiguredExternalEventProcessor<T> configuredEngine = onInvocation(graph, extractor);
    EventProcessorRuntimeParams<T> runtimeParams =
        new EventProcessorRuntimeParams<>
        (
            configuredEngine.getBindingParams(),
            false,
            configExtractor,
            streamPipesClient
        );

    return new StandaloneExternalEventProcessorRuntime<>(configuredEngine.getEngineSupplier(),
        runtimeParams);
  }
}
