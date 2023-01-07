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
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.declarer.EventSinkDeclarer;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneEventSinkRuntime;

public abstract class StandaloneEventSinkDeclarer<T extends
    EventSinkBindingParams> extends EventSinkDeclarer<T, StandaloneEventSinkRuntime<T>> {

  @Override
  public StandaloneEventSinkRuntime<T> getRuntime(DataSinkInvocation graph,
                                                  DataSinkParameterExtractor extractor,
                                                  ConfigExtractor configExtractor,
                                                  StreamPipesClient streamPipesClient) {

    ConfiguredEventSink<T> configuredEngine = onInvocation(graph, extractor);
    EventSinkRuntimeParams<T> runtimeParams =
        new EventSinkRuntimeParams<>
        (
            configuredEngine.getBindingParams(),
            false,
            configExtractor,
            streamPipesClient
        );

    return new StandaloneEventSinkRuntime<>(configuredEngine.getEngineSupplier(), runtimeParams);
  }

  public abstract ConfiguredEventSink<T> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor);

}
