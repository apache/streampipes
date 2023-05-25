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

package org.apache.streampipes.sdk.extractor;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;

import java.util.ArrayList;
import java.util.List;

public class ProcessingElementParameterExtractor extends AbstractParameterExtractor<DataProcessorInvocation>
    implements IDataProcessorParameterExtractor {

  public ProcessingElementParameterExtractor(DataProcessorInvocation sepaElement) {
    super(sepaElement);
  }

  public static ProcessingElementParameterExtractor from(DataProcessorInvocation sepaElement) {
    return new ProcessingElementParameterExtractor(sepaElement);
  }

  @Override
  public String outputTopic() {
    return sepaElement
        .getOutputStream()
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }

  @Override
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
