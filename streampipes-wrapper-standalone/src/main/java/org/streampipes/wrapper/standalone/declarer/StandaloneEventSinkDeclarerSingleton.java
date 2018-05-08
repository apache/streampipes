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

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.declarer.EventSinkDeclarer;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.param.StandaloneEventSinkRuntimeParams;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventSinkRuntime;

public abstract class StandaloneEventSinkDeclarerSingleton<B extends
        EventSinkBindingParams> extends EventSinkDeclarer<B, StandaloneEventSinkRuntime> {

  @Override
  public StandaloneEventSinkRuntime getRuntime(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

    ConfiguredEventSink<B> configuredEngine = onInvocation(graph);
    StandaloneEventSinkRuntimeParams<B> runtimeParams = new StandaloneEventSinkRuntimeParams<>
            (configuredEngine.getEngineSupplier(), configuredEngine.getBindingParams(), true);

    return new StandaloneEventSinkRuntime(runtimeParams);
  }

  public abstract ConfiguredEventSink<B> onInvocation(DataSinkInvocation graph);
}
