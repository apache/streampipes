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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.execution.http.DetachExtensionRequest;
import org.apache.streampipes.manager.execution.http.InvokeExtensionRequest;
import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PipelinePreview {

  private static final Logger LOG = LoggerFactory.getLogger(PipelinePreview.class);

  public PipelinePreviewModel initiatePreview(Pipeline pipeline,
                                              ExtensionServiceRequestManager requestManager) {
    String previewId = generatePreviewId();
    var elementIdMappings = new HashMap<String, String>();
    pipeline.setActions(new ArrayList<>());
    List<NamedStreamPipesEntity> pipelineElements = new ArrayList<>(
        new PipelineVerificationHandlerV2(pipeline, requestManager)
            .verifyAndBuildGraphs(true)
            .modifiedPipelineElements()
    );

    rewriteElementIds(pipelineElements, elementIdMappings);
    invokeGraphs(filter(pipelineElements), requestManager);
    storeGraphs(previewId, pipelineElements);

    LOG.info("Preview pipeline {} started", previewId);

    return makePreviewModel(previewId, elementIdMappings);
  }

  public void deletePreview(String previewId,
                            ExtensionServiceRequestManager requestManager) {
    List<NamedStreamPipesEntity> graphs = ActivePipelinePreviews.INSTANCE.getInvocationGraphs(previewId);
    detachGraphs(filter(graphs), requestManager);
    deleteGraphs(previewId);
    LOG.info("Preview pipeline {} stopped", previewId);
  }

  public Map<String, SpDataStream> getPipelineElementPreviewStreams(String previewId) throws IllegalArgumentException {

    return ActivePipelinePreviews
        .INSTANCE
        .getInvocationGraphs(previewId)
        .stream()
        .filter(this::isProcessorOrStream)
        .collect(Collectors.toMap(
            NamedStreamPipesEntity::getElementId,
            this::extractStreamFromElement,
            (existing, replacement) -> existing // keep the first stream in case of duplicate keys
        ));
  }

  private boolean isProcessorOrStream(NamedStreamPipesEntity pe) {
    return pe instanceof DataProcessorInvocation || pe instanceof SpDataStream;
  }

  private SpDataStream extractStreamFromElement(NamedStreamPipesEntity element) {
    if (element instanceof DataProcessorInvocation) {
      return ((DataProcessorInvocation) element).getOutputStream();
    } else if (element instanceof SpDataStream) {
      return (SpDataStream) element;
    } else {
      throw new IllegalArgumentException("Unsupported graph type: " + element.getClass()
                                                                             .getSimpleName());
    }

  }

  private void rewriteElementIds(
      List<NamedStreamPipesEntity> pipelineElements,
      Map<String, String> elementIdMappings
  ) {
    pipelineElements
        .forEach(pe -> {
          if (pe instanceof DataProcessorInvocation) {
            var originalElementId = pe.getElementId();
            var newElementId = (
                String.format(
                    "%s:%s",
                    StringUtils.substringBeforeLast(pe.getElementId(), ":"),
                    RandomStringUtils.randomAlphanumeric(5)
                )
            );
            pe.setElementId(newElementId);
            elementIdMappings.put(originalElementId, newElementId);
          } else {
            elementIdMappings.put(pe.getElementId(), pe.getElementId());
          }
        });
  }

  private SpServiceRegistration findSelectedService(InvocableStreamPipesEntity g) throws NoServiceEndpointsAvailableException {
    return new ExtensionsServiceEndpointGenerator()
        .selectService(
            g.getAppId(),
            ExtensionsServiceEndpointUtils.getPipelineElementType(g),
            Set.of()
        );
  }

  private void invokeGraphs(List<InvocableStreamPipesEntity> graphs,
                            ExtensionServiceRequestManager requestManager) {
    graphs.forEach(g -> {
      try {
        var service = findSelectedService(g);
        g.setSelectedEndpointUrl(service.getServiceUrl());
        g.setSelectedServiceId(service.getSvcId());
        new InvokeExtensionRequest(requestManager).execute(g, null);
      } catch (NoServiceEndpointsAvailableException e) {
        LOG.warn("No endpoint found for pipeline element {}", g.getAppId());
      }
    });
  }

  private void detachGraphs(List<InvocableStreamPipesEntity> graphs,
                            ExtensionServiceRequestManager requestManager) {
    graphs.forEach(g -> {
      new DetachExtensionRequest(requestManager).execute(g, null);
    });
  }

  private void deleteGraphs(String previewId) {
    ActivePipelinePreviews.INSTANCE.removePreview(previewId);
  }

  private void storeGraphs(
      String previewId,
      List<NamedStreamPipesEntity> graphs
  ) {
    ActivePipelinePreviews.INSTANCE.addActivePreview(previewId, graphs);
  }

  private String generatePreviewId() {
    return UUID.randomUUID()
               .toString();
  }

  private PipelinePreviewModel makePreviewModel(
      String previewId,
      Map<String, String> elementIdMappings
  ) {
    PipelinePreviewModel previewModel = new PipelinePreviewModel();
    previewModel.setPreviewId(previewId);
    previewModel.setElementIdMappings(elementIdMappings);

    return previewModel;
  }

  private List<InvocableStreamPipesEntity> filter(List<NamedStreamPipesEntity> graphs) {
    List<InvocableStreamPipesEntity> dataProcessors = new ArrayList<>();
    graphs.stream()
          .filter(g -> g instanceof DataProcessorInvocation)
          .forEach(p -> dataProcessors.add((DataProcessorInvocation) p));

    return dataProcessors;
  }
}
