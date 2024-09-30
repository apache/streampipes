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

package org.apache.streampipes.manager.pipeline.compact.generation;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.compact.CompactPipeline;
import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;

public class PipelineElementConfigurationStep implements CompactPipelineGenerator {

  private static final String StreamType = "stream";
  private static final String ProcessorType = "processor";
  private static final String SinkType = "sink";

  private final IPipelineElementDescriptionStorage storage;

  public PipelineElementConfigurationStep(IPipelineElementDescriptionStorage storage) {
    this.storage = storage;
  }

  @Override
  public void apply(Pipeline pipeline,
                    CompactPipeline compactPipeline) throws Exception {
    compactPipeline.pipelineElements().forEach(pe -> {
      if (pe.type().equalsIgnoreCase(StreamType)) {
        pipeline.getStreams().add(makeStream(pe));
      } else if (pe.type().equalsIgnoreCase(ProcessorType)) {
        pipeline.getSepas().add(makeProcessor(pe));
      } else if (pe.type().equalsIgnoreCase(SinkType)) {
        pipeline.getActions().add(makeSink(pe));
      }
    });
  }

  public SpDataStream makeStream(CompactPipelineElement pipelineElement) {
    var element = storage.getDataStreamById(pipelineElement.id());
    return new DataStreamPipelineElementGenerator().generate(element, pipelineElement);
  }

  public DataProcessorInvocation makeProcessor(CompactPipelineElement pipelineElement) {
    var element = storage.getDataProcessorByAppId(pipelineElement.id());
    var invocation = new DataProcessorInvocation(element);
    return new DataProcessorPipelineElementGenerator(new InvocablePipelineElementGenerator<>())
        .generate(invocation, pipelineElement);
  }

  public DataSinkInvocation makeSink(CompactPipelineElement pipelineElement) {
    var element = storage.getDataSinkByAppId(pipelineElement.id());
    var invocation = new DataSinkInvocation(element);
    return new DataSinkPipelineElementGenerator(new InvocablePipelineElementGenerator<>())
        .generate(invocation, pipelineElement);
  }

}
