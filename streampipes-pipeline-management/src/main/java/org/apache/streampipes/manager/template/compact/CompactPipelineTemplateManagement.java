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

package org.apache.streampipes.manager.template.compact;

import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.manager.pipeline.compact.generation.PipelineElementConfigurationStep;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModificationResult;
import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.model.template.PipelineTemplateGenerationRequest;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.streampipes.manager.pipeline.compact.generation.InvocablePipelineElementGenerator.ID_PREFIX;

public class CompactPipelineTemplateManagement {

  private final IPipelineElementDescriptionStorage storage;
  private final CRUDStorage<CompactPipelineTemplate> templateStorage;

  public CompactPipelineTemplateManagement(CRUDStorage<CompactPipelineTemplate> templateStorage,
                                           IPipelineElementDescriptionStorage descriptionStorage) {
    this.templateStorage = templateStorage;
    this.storage = descriptionStorage;
  }

  public PipelineModificationResult makePipeline(PipelineTemplateGenerationRequest request) throws Exception {
    var template = request.template();
    if (request.streams() != null && !request.streams().isEmpty()) {
      request.streams().forEach((key, value) -> {
        var stream = storage.getDataStreamById(value);
        template.getPipeline().add(new CompactPipelineElement(
            "stream",
            key,
            stream.getElementId(),
            List.of(),
            List.of()
        ));
      });
    }
    var pipeline = makePipeline(template);

    return new PipelineVerificationHandlerV2(pipeline).makeModifiedPipeline();
  }

  private Pipeline makePipeline(CompactPipelineTemplate template) throws Exception {
    var pipeline = new Pipeline();
    template.getPipeline().forEach(pe -> {
      List<String> connectedToList = pe.connectedTo();
      Set<String> validIds = template.getPipeline().stream()
          .map(CompactPipelineElement::ref)
          .collect(Collectors.toSet());
      connectedToList.removeIf(connectedId -> !validIds.contains(connectedId));
    });
    new PipelineElementConfigurationStep(storage).apply(pipeline, template.toCompactPipeline());

    return pipeline;
  }

  public Map<String, List<List<String>>> getStreamsForTemplate(String pipelineTemplateId) throws Exception {
    var template = Optional.ofNullable(templateStorage.getElementById(pipelineTemplateId))
        .orElseThrow(() -> new IllegalArgumentException("Template " + pipelineTemplateId + " not found"));

    var pipeline = makePipeline(template);
    var allDataStreams = storage.getAllDataStreams();
    var requiredStreamInputs = template
        .getPlaceholders()
        .requiredStreamInputs()
        .stream()
        .map(c -> ID_PREFIX + c)
        .toList();

    return new MatchingStreamFinder().findMatchedStreams(pipeline, requiredStreamInputs, allDataStreams);
  }
}
