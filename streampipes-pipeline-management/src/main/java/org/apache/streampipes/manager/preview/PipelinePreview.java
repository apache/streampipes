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
package org.apache.streampipes.manager.preview;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.execution.http.DetachHttpRequest;
import org.apache.streampipes.manager.execution.http.InvokeHttpRequest;
import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.preview.PipelinePreviewModel;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PipelinePreview {

  private static final Logger LOG = LoggerFactory.getLogger(PipelinePreview.class);

  public PipelinePreviewModel initiatePreview(Pipeline pipeline) {
    String previewId = generatePreviewId();
    var elementIdMappings = new HashMap<String, String>();
    pipeline.setActions(new ArrayList<>());
    List<NamedStreamPipesEntity> pipelineElements = new ArrayList<>(
        new PipelineVerificationHandlerV2(pipeline)
            .verifyAndBuildGraphs(true)
            .modifiedPipelineElements()
    );

    rewriteElementIds(pipelineElements, elementIdMappings);
    invokeGraphs(filter(pipelineElements));
    storeGraphs(previewId, pipelineElements);

    LOG.info("Preview pipeline {} started", previewId);

    return makePreviewModel(previewId, elementIdMappings);
  }

  public void deletePreview(String previewId) {
    List<NamedStreamPipesEntity> graphs = ActivePipelinePreviews.INSTANCE.getInvocationGraphs(previewId);
    detachGraphs(filter(graphs));
    deleteGraphs(previewId);
    LOG.info("Preview pipeline {} stopped", previewId);
  }

  public Map<String, SpDataStream> getPipelineElementPreviewStreams(String previewId) throws IllegalArgumentException {
    return ActivePipelinePreviews
        .INSTANCE
        .getInvocationGraphs(previewId)
        .stream()
        .filter(graph -> graph instanceof DataProcessorInvocation || graph instanceof SpDataStream)
        .collect(Collectors.toMap(
            NamedStreamPipesEntity::getElementId,
            graph -> {
              if (graph instanceof DataProcessorInvocation) {
                return ((DataProcessorInvocation) graph).getOutputStream();
              } else {
                return (SpDataStream) graph;
              }
            }
        ));
  }

  private void rewriteElementIds(List<NamedStreamPipesEntity> pipelineElements,
                                 Map<String, String> elementIdMappings) {
    pipelineElements
        .forEach(pe -> {
          if (pe instanceof DataProcessorInvocation) {
            var originalElementId = pe.getElementId();
            var newElementId = (String.format(
                "%s:%s",
                StringUtils.substringBeforeLast(pe.getElementId(), ":"),
                RandomStringUtils.randomAlphanumeric(5)));
            pe.setElementId(newElementId);
            elementIdMappings.put(originalElementId, newElementId);
          } else {
            elementIdMappings.put(pe.getElementId(), pe.getElementId());
          }
        });
  }

  private String findSelectedEndpoint(InvocableStreamPipesEntity g) throws NoServiceEndpointsAvailableException {
    return new ExtensionsServiceEndpointGenerator()
        .getEndpointResourceUrl(
            g.getAppId(),
            ExtensionsServiceEndpointUtils.getPipelineElementType(g)
        );
  }

  private void invokeGraphs(List<InvocableStreamPipesEntity> graphs) {
    graphs.forEach(g -> {
      try {
        g.setSelectedEndpointUrl(findSelectedEndpoint(g));
        new InvokeHttpRequest().execute(g, g.getSelectedEndpointUrl(), null);
      } catch (NoServiceEndpointsAvailableException e) {
        LOG.warn("No endpoint found for pipeline element {}", g.getAppId());
      }
    });
  }

  private void detachGraphs(List<InvocableStreamPipesEntity> graphs) {
    graphs.forEach(g -> {
      String endpointUrl = g.getSelectedEndpointUrl() + g.getDetachPath();
      new DetachHttpRequest().execute(g, endpointUrl, null);
    });
  }

  private void deleteGraphs(String previewId) {
    ActivePipelinePreviews.INSTANCE.removePreview(previewId);
  }

  private void storeGraphs(String previewId,
                           List<NamedStreamPipesEntity> graphs) {
    ActivePipelinePreviews.INSTANCE.addActivePreview(previewId, graphs);
  }

  private String generatePreviewId() {
    return UUID.randomUUID().toString();
  }

  private PipelinePreviewModel makePreviewModel(String previewId,
                                                Map<String, String> elementIdMappings) {
    PipelinePreviewModel previewModel = new PipelinePreviewModel();
    previewModel.setPreviewId(previewId);
    previewModel.setElementIdMappings(elementIdMappings);

    return previewModel;
  }

  private List<String> collectElementIds(List<NamedStreamPipesEntity> graphs) {
    return graphs
        .stream()
        .map(NamedStreamPipesEntity::getElementId)
        .collect(Collectors.toList());
  }

  private List<InvocableStreamPipesEntity> filter(List<NamedStreamPipesEntity> graphs) {
    List<InvocableStreamPipesEntity> dataProcessors = new ArrayList<>();
    graphs.stream()
        .filter(g -> g instanceof DataProcessorInvocation)
        .forEach(p -> dataProcessors.add((DataProcessorInvocation) p));

    return dataProcessors;
  }
}
