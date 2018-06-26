/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.examples.jvm.processor.projection;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class ProjectionParameters extends EventProcessorBindingParams {

  private List<String> outputKeys;

  public ProjectionParameters(DataProcessorInvocation graph, List<String> outputKeys) {
    super(graph);
    this.outputKeys = outputKeys;
  }

  public List<String> getOutputKeys() {
    return outputKeys;
  }
}
