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

package org.streampipes.wrapper.standalone.engine;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.Map;
import java.util.Optional;

public abstract class StandaloneEventProcessorEngine<B extends EventProcessorBindingParams> implements EventProcessor<B> {

  private Optional<SpOutputCollector> collectorOpt;

  @Override
  public void bind(B parameters, SpOutputCollector collector) {
    collectorOpt = Optional.of(collector);
    onInvocation(parameters, parameters.getGraph());
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
      if (collectorOpt.isPresent()) {
        onEvent(event, sourceInfo, collectorOpt.get());
      } else {
        throw new IllegalArgumentException("");
      }
  }

  @Override
  public void discard() {
    this.collectorOpt = Optional.empty();
    onDetach();
  }

  public abstract void onInvocation(B params, DataProcessorInvocation graph);

  public abstract void onEvent(Map<String, Object> event, String sourceInfo, SpOutputCollector
          collector);

  public abstract void onDetach();
}
