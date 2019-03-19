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

package org.streampipes.wrapper.params.binding;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.output.PropertyRenameRule;
import org.streampipes.model.util.SchemaUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BindingParams<I extends InvocableStreamPipesEntity> implements Serializable {
  private static final long serialVersionUID = 1L;

  protected I graph;
  private List<InputStreamParams> inputStreamParams = new ArrayList<>();

  private final Map<String, Map<String, Object>> inEventTypes;

  BindingParams(I graph) {
    this.graph = graph;
    this.inEventTypes = new HashMap<>();
    buildInEventTypes();
    buildInputStreamParams();
  }

  private void buildInEventTypes() {
    graph.getInputStreams().forEach(is ->
            inEventTypes.put(is.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName(), SchemaUtils
                    .toRuntimeMap
                            (is.getEventSchema().getEventProperties())));
  }

  private void buildInputStreamParams() {
    for (int i = 0; i < graph.getInputStreams().size(); i++) {
      inputStreamParams.add(new InputStreamParams(i, graph.getInputStreams().get(i),
              getRenameRules()));
    }
  }

  public I getGraph() {
    return graph;
  }

  public List<InputStreamParams> getInputStreamParams() {
    return inputStreamParams;
  }

  public Map<String, Map<String, Object>> getInEventTypes() {
    return inEventTypes;
  }

  protected abstract List<PropertyRenameRule> getRenameRules();
}
