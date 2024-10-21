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

package org.apache.streampipes.manager.pipeline.compact;

import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.manager.pipeline.compact.generation.CompactPipelineConverter;
import org.apache.streampipes.manager.pipeline.compact.generation.PipelineElementConfigurationStep;
import org.apache.streampipes.model.connect.adapter.compact.CreateOptions;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModificationResult;
import org.apache.streampipes.model.pipeline.compact.CompactPipeline;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;

import java.util.UUID;

public class CompactPipelineManagement {

  private final IPipelineElementDescriptionStorage storage;

  public CompactPipelineManagement(IPipelineElementDescriptionStorage storage) {
    this.storage = storage;
  }

  public PipelineModificationResult makePipeline(CompactPipeline compactPipeline) throws Exception {
    var pipeline = new Pipeline();
    applyPipelineBasics(compactPipeline, pipeline);

    new PipelineElementConfigurationStep(storage).apply(pipeline, compactPipeline);

    return new PipelineVerificationHandlerV2(pipeline).makeModifiedPipeline();
  }

  public CompactPipeline convertPipeline(Pipeline pipeline) {
    return new CompactPipeline(
        pipeline.getElementId() != null ? pipeline.getElementId() : UUID.randomUUID().toString(),
        pipeline.getName(),
        pipeline.getDescription(),
        new CompactPipelineConverter().convert(pipeline),
        new CreateOptions(null, false)
    );
  }

  private void applyPipelineBasics(CompactPipeline compactPipeline,
                                   Pipeline pipeline) {
    pipeline.setElementId(compactPipeline.id());
    pipeline.setName(compactPipeline.name());
    pipeline.setDescription(compactPipeline.description());
  }
}
