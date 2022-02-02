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


package org.apache.streampipes.manager.recommender;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

public class PartialPipelineGenerator {

  private final String baseRecDomId;
  private final AllElementsProvider elementsProvider;

  public PartialPipelineGenerator(String baseRecDomId,
                                  AllElementsProvider elementsProvider) {
    this.baseRecDomId = baseRecDomId;
    this.elementsProvider = elementsProvider;
  }

  public Pipeline makePartialPipeline() {
    Pipeline pipeline = new Pipeline();
    List<SpDataStream> streams = new ArrayList<>();
    List<DataProcessorInvocation> processors = new ArrayList<>();

    findConnectedElements(elementsProvider.findElement(this.baseRecDomId), streams, processors);

    pipeline.setStreams(streams);
    pipeline.setSepas(processors);
    return pipeline;
  }

  private void findConnectedElements(NamedStreamPipesEntity pipelineElement,
                                     List<SpDataStream> streams,
                                     List<DataProcessorInvocation> processors) {
    if (pipelineElement instanceof SpDataStream) {
      streams.add((SpDataStream) pipelineElement);
    } else if (pipelineElement instanceof DataProcessorInvocation) {
      DataProcessorInvocation processor = (DataProcessorInvocation) pipelineElement;
      processors.add(processor);
      processor.getConnectedTo().forEach(p -> {
        NamedStreamPipesEntity connectedElement = elementsProvider.findElement(p);
        findConnectedElements(connectedElement, streams, processors);
      });
    }
  }


}
