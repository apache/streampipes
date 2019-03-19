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

package org.streampipes.sdk.extractor;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomOutputStrategy;

import java.util.ArrayList;
import java.util.List;

public class ProcessingElementParameterExtractor extends AbstractParameterExtractor<DataProcessorInvocation> {

  public static ProcessingElementParameterExtractor from(DataProcessorInvocation sepaElement) {
    return new ProcessingElementParameterExtractor(sepaElement);
  }

  public ProcessingElementParameterExtractor(DataProcessorInvocation sepaElement) {
    super(sepaElement);
  }

  public String outputTopic() {
    return sepaElement
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  public List<String> outputKeySelectors() {
    return sepaElement
            .getOutputStrategies()
            .stream()
            .filter(CustomOutputStrategy.class::isInstance)
            .map(o -> (CustomOutputStrategy) o)
            .findFirst()
            .map(CustomOutputStrategy::getSelectedPropertyKeys)
            .orElse(new ArrayList<>());

  }

}
