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
import org.apache.streampipes.resource.management.SpResourceManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PipelinePreview {

  private static final Logger LOG = LoggerFactory.getLogger(PipelinePreview.class);

  /**
   * How long a preview stays alive without an attached result stream: after it was created,
   * and each time a result stream reaches its maximum duration. The client re-attaches within
   * this time while the preview is open; otherwise the preview is released.
   */
  static final Duration STREAM_ATTACH_GRACE_PERIOD = Duration.ofSeconds(30);

  /**
   * Maximum lifetime of a preview, also while a client stays attached, so that a forgotten
   * open editor does not keep temporary pipeline elements running.
   */
  static final Duration MAX_PREVIEW_DURATION = Duration.ofMinutes(5);

  private final SpResourceManager resourceManager;
  private final Duration streamAttachGracePeriod;
  private final Duration maxPreviewDuration;

  public PipelinePreview(SpResourceManager resourceManager) {
    this(resourceManager, STREAM_ATTACH_GRACE_PERIOD, MAX_PREVIEW_DURATION);
  }

  PipelinePreview(SpResourceManager resourceManager,
                  Duration streamAttachGracePeriod) {
    this(resourceManager, streamAttachGracePeriod, MAX_PREVIEW_DURATION);
  }

  PipelinePreview(SpResourceManager resourceManager,
                  Duration streamAttachGracePeriod,
                  Duration maxPreviewDuration) {
    this.resourceManager = resourceManager;
    this.streamAttachGracePeriod = streamAttachGracePeriod;
    this.maxPreviewDuration = maxPreviewDuration;
  }

  public PipelinePreviewModel initiatePreview(Pipeline pipeline,
                                              ExtensionServiceRequestManager requestManager) {
    String previewId = generatePreviewId();
    var elementIdMappings = new HashMap<String, String>();
    var elementErrors = new HashMap<String, String>();
    pipeline.setActions(new ArrayList<>());
    List<NamedStreamPipesEntity> pipelineElements = new ArrayList<>(
        new PipelineVerificationHandlerV2(resourceManager.getServiceDiscovery(), pipeline, requestManager)
            .verifyAndBuildGraphs(true)
            .modifiedPipelineElements()
    );

    rewriteElementIds(pipelineElements, elementIdMappings);
    var failedInvocations = invokeGraphs(filter(pipelineElements), requestManager);
    reportFailures(failedInvocations, elementIdMappings, elementErrors);
    storeGraphs(previewId, elementsToTrack(pipelineElements, failedInvocations));
    limitLifetime(previewId, requestManager);

    LOG.info("Preview pipeline {} started ({} element(s) failed)", previewId, failedInvocations.size());

    return makePreviewModel(previewId, elementIdMappings, elementErrors);
  }

  /**
   * Stops a preview and detaches its temporary pipeline elements. Safe to call more than once:
   * an explicit stop request and the end of the preview stream may both trigger it.
   */
  public void deletePreview(String previewId,
                            ExtensionServiceRequestManager requestManager) {
    ActivePipelinePreviews.INSTANCE.removePreview(previewId).ifPresent(graphs -> {
      detachGraphs(filter(graphs), requestManager);
      LOG.info("Preview pipeline {} stopped", previewId);
    });
  }

  public boolean isActive(String previewId) {
    return ActivePipelinePreviews.INSTANCE.isActive(previewId);
  }

  /**
   * A result stream is attached; the preview now lives until the client disconnects, stops it
   * or lets the stream expire without re-attaching.
   */
  public void onStreamAttached(String previewId) {
    ActivePipelinePreviews.INSTANCE.cancelScheduledRelease(previewId);
  }

  /**
   * The result stream reached its maximum duration. The preview keeps running for the grace
   * period so that an open client can re-attach without restarting the pipeline elements.
   */
  public void onStreamExpired(String previewId,
                              ExtensionServiceRequestManager requestManager) {
    if (isActive(previewId)) {
      releaseUnlessStreamAttaches(previewId, requestManager);
    }
  }

  /**
   * Releases the preview if no result stream attaches within the grace period, and in any case
   * once it reaches its maximum duration.
   */
  void limitLifetime(String previewId,
                     ExtensionServiceRequestManager requestManager) {
    releaseUnlessStreamAttaches(previewId, requestManager);
    ActivePipelinePreviews.INSTANCE.limitLifetime(
        previewId,
        maxPreviewDuration,
        () -> {
          LOG.info("Preview pipeline {} reached its maximum duration of {}", previewId, maxPreviewDuration);
          deletePreview(previewId, requestManager);
        }
    );
  }

  private void releaseUnlessStreamAttaches(String previewId,
                                           ExtensionServiceRequestManager requestManager) {
    ActivePipelinePreviews.INSTANCE.scheduleRelease(
        previewId,
        streamAttachGracePeriod,
        () -> deletePreview(previewId, requestManager)
    );
  }

  public Map<String, SpDataStream> getPipelineElementPreviewStreams(String previewId) throws IllegalArgumentException {

    return ActivePipelinePreviews
        .INSTANCE
        .getInvocationGraphs(previewId)
        .orElseThrow(() -> new IllegalArgumentException("No active preview with id " + previewId))
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
    return new ExtensionsServiceEndpointGenerator(resourceManager.getServiceDiscovery())
        .selectService(
            g.getAppId(),
            ExtensionsServiceEndpointUtils.getPipelineElementType(g),
            Set.of()
        );
  }

  /**
   * Why an element could not be started for the preview.
   *
   * @param requestSent whether the invocation request may have reached an extensions service. A failed
   *                    request can still have started the element there (e.g. after a timeout), so such
   *                    an element must be detached when the preview stops.
   */
  record InvocationFailure(String message, boolean requestSent) {
  }

  /**
   * Invokes the given elements, one by one, so that a failure of one element does not leave
   * the elements invoked before it untracked.
   *
   * @return failures keyed by the (preview) element id of each element that could not be started
   */
  private Map<String, InvocationFailure> invokeGraphs(List<InvocableStreamPipesEntity> graphs,
                                                      ExtensionServiceRequestManager requestManager) {
    var failures = new HashMap<String, InvocationFailure>();
    graphs.forEach(g -> {
      SpServiceRegistration service;
      try {
        service = findSelectedService(g);
      } catch (NoServiceEndpointsAvailableException e) {
        LOG.warn("No endpoint found for pipeline element {}", g.getAppId());
        failures.put(g.getElementId(), new InvocationFailure(
            "No extensions service providing " + g.getAppId() + " is available", false));
        return;
      } catch (RuntimeException e) {
        LOG.warn("Could not select an extensions service for pipeline element {}", g.getAppId(), e);
        failures.put(g.getElementId(), new InvocationFailure(
            "Could not select an extensions service: " + e.getMessage(), false));
        return;
      }
      g.setSelectedEndpointUrl(service.getServiceUrl());
      g.setSelectedServiceId(service.getSvcId());
      try {
        var status = new InvokeExtensionRequest(requestManager, resourceManager).execute(g, null);
        if (!status.isSuccess()) {
          failures.put(g.getElementId(), new InvocationFailure(Objects.requireNonNullElse(
              status.getOptionalMessage(), "The extensions service rejected the invocation"), true));
        }
      } catch (RuntimeException e) {
        LOG.warn("Could not invoke pipeline element {} for preview", g.getAppId(), e);
        failures.put(g.getElementId(), new InvocationFailure(
            Objects.requireNonNullElse(e.getMessage(), "The invocation failed"), true));
      }
    });
    return failures;
  }

  /**
   * The elements a preview keeps track of, i.e. that are consumed by the preview stream and
   * detached when the preview stops. Only elements whose invocation request was never sent are
   * left out; every element that may be running is detached.
   */
  static List<NamedStreamPipesEntity> elementsToTrack(List<NamedStreamPipesEntity> pipelineElements,
                                                      Map<String, InvocationFailure> failedInvocations) {
    return pipelineElements.stream()
        .filter(pe -> {
          var failure = failedInvocations.get(pe.getElementId());
          return failure == null || failure.requestSent();
        })
        .toList();
  }

  private void reportFailures(Map<String, InvocationFailure> failedInvocations,
                              Map<String, String> elementIdMappings,
                              Map<String, String> elementErrors) {
    elementIdMappings.forEach((originalElementId, previewElementId) -> {
      if (failedInvocations.containsKey(previewElementId)) {
        elementErrors.put(originalElementId, failedInvocations.get(previewElementId).message());
      }
    });
  }

  private void detachGraphs(List<InvocableStreamPipesEntity> graphs,
                            ExtensionServiceRequestManager requestManager) {
    graphs.forEach(g -> {
      new DetachExtensionRequest(requestManager, resourceManager).execute(g, null);
    });
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
      Map<String, String> elementIdMappings,
      Map<String, String> elementErrors
  ) {
    PipelinePreviewModel previewModel = new PipelinePreviewModel();
    previewModel.setPreviewId(previewId);
    previewModel.setElementIdMappings(elementIdMappings);
    previewModel.setElementErrors(elementErrors);
    previewModel.setMaxDurationMillis(maxPreviewDuration.toMillis());

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
