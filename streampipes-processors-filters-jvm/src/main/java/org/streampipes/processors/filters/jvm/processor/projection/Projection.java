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

package org.streampipes.processors.filters.jvm.processor.projection;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Projection extends StandaloneEventProcessorEngine<ProjectionParameters> {

  private List<String> outputKeys;

  public Projection(ProjectionParameters params) {
    super(params);
  }

  @Override
  public void onInvocation(ProjectionParameters projectionParameters, DataProcessorInvocation dataProcessorInvocation) {
    this.outputKeys = projectionParameters.getOutputKeys();
  }

  @Override
  public void onEvent(Map<String, Object> in, String sourceInfo, SpOutputCollector out) {
    Map<String, Object> outEvent = new HashMap<>();
    for(String outputKey : outputKeys) {
      outEvent.put(outputKey, in.get(outputKey));
    }
    out.onEvent(outEvent);
  }

  @Override
  public void onDetach() {

  }
}
