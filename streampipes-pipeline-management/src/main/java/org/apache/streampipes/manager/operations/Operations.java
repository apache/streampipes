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

package org.apache.streampipes.manager.operations;

import org.apache.streampipes.commons.exceptions.NoSuitableSepasAvailableException;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.endpoint.EndpointItemFetcher;
import org.apache.streampipes.manager.execution.http.PipelineExecutor;
import org.apache.streampipes.manager.execution.http.PipelineStorageService;
import org.apache.streampipes.manager.matching.DataSetGroundingSelector;
import org.apache.streampipes.manager.matching.PipelineVerificationHandler;
import org.apache.streampipes.manager.recommender.ElementRecommender;
import org.apache.streampipes.manager.remote.ContainerProvidedOptionsHandler;
import org.apache.streampipes.manager.runtime.PipelineElementRuntimeInfoFetcher;
import org.apache.streampipes.manager.template.PipelineTemplateGenerator;
import org.apache.streampipes.manager.template.PipelineTemplateInvocationGenerator;
import org.apache.streampipes.manager.template.PipelineTemplateInvocationHandler;
import org.apache.streampipes.manager.topic.WildcardTopicGenerator;
import org.apache.streampipes.manager.verification.extractor.TypeExtractor;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpointItem;
import org.apache.streampipes.model.message.DataSetModificationMessage;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendationMessage;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;


/**
 * class that provides several (partial) pipeline verification methods
 *
 * @author riemer
 */

public class Operations {


  public static PipelineModificationMessage validatePipeline(Pipeline pipeline, boolean isPartial) throws Exception {
    return validatePipeline(pipeline, isPartial, "");
  }

  /**
   * This method is a fix for the streamsets integration. Remove the username from the signature when you don't need it anymore
   *
   * @param pipeline
   * @param isPartial
   * @param username
   * @return
   * @throws Exception
   */
  public static PipelineModificationMessage validatePipeline(Pipeline pipeline, boolean isPartial, String username)
          throws Exception {
    PipelineVerificationHandler validator = new PipelineVerificationHandler(
            pipeline);
    return validator
            .validateConnection()
            .computeMappingProperties()
            .storeConnection()
            .getPipelineModificationMessage();
  }

  public static DataSetModificationMessage updateDataSet(SpDataSet dataSet) {
    return new DataSetGroundingSelector(dataSet).selectGrounding();
  }

  public static Message verifyAndAddElement(String graphData,
                                            String username,
                                            boolean publicElement) throws SepaParseException {
    return new TypeExtractor(graphData).getTypeVerifier().verifyAndAdd(username, publicElement);
  }

  public static Message verifyAndUpdateElement(String graphData) throws SepaParseException {
    return new TypeExtractor(graphData).getTypeVerifier().verifyAndUpdate();
  }

  public static PipelineElementRecommendationMessage findRecommendedElements(Pipeline partialPipeline) throws NoSuitableSepasAvailableException {
    return new ElementRecommender(partialPipeline).findRecommendedElements();
  }

  public static void storePipeline(Pipeline pipeline) {
    new PipelineStorageService(pipeline).addPipeline();
  }

  public static void updatePipeline(Pipeline pipeline) {
    new PipelineStorageService(pipeline).updatePipeline();
  }

  public static PipelineOperationStatus startPipeline(
          Pipeline pipeline) {
    return startPipeline(pipeline,true, true, false);
  }

  public static PipelineOperationStatus startPipeline(
          Pipeline pipeline, boolean visualize, boolean storeStatus,
          boolean monitor) {
    return new PipelineExecutor(pipeline, visualize, storeStatus, monitor, false).startPipeline();
  }

  public static PipelineOperationStatus stopPipeline(
          Pipeline pipeline, boolean forceStop) {
    return stopPipeline(pipeline, true, true, false, forceStop);
  }

  public static List<PipelineOperationStatus> stopAllPipelines(boolean forceStop) {
    List<PipelineOperationStatus> status = new ArrayList<>();
    List<Pipeline> pipelines =
            StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getAllPipelines();

    pipelines.forEach(p -> {
      if (p.isRunning()) {
        status.add(Operations.stopPipeline(p, forceStop));
      }
    });
    return status;
  }

  public static PipelineOperationStatus stopPipeline(Pipeline pipeline,
                                                     boolean visualize,
                                                     boolean storeStatus,
                                                     boolean monitor,
                                                     boolean forceStop) {
    return new PipelineExecutor(pipeline, visualize, storeStatus, monitor, forceStop).stopPipeline();
  }

  public static List<ExtensionsServiceEndpointItem> getEndpointUriContents(List<ExtensionsServiceEndpoint> endpoints) {
    return new EndpointItemFetcher(endpoints).getItems();
  }

  public static SpDataStream updateActualTopic(SpDataStream stream) {
    return new WildcardTopicGenerator(stream).computeActualTopic();
  }

  public static RuntimeOptionsResponse fetchRemoteOptions(RuntimeOptionsRequest request) {
    return new ContainerProvidedOptionsHandler().fetchRemoteOptions(request);
  }

  public static List<PipelineTemplateDescription> getAllPipelineTemplates() {
    return new PipelineTemplateGenerator().getAllPipelineTemplates();
  }

  public static List<PipelineTemplateDescription> getCompatiblePipelineTemplates(String streamId) {
    return new PipelineTemplateGenerator().getCompatibleTemplates(streamId);
  }

  public static PipelineOperationStatus handlePipelineTemplateInvocation(String username, PipelineTemplateInvocation pipelineTemplateInvocation) {
    return new PipelineTemplateInvocationHandler(username, pipelineTemplateInvocation).handlePipelineInvocation();
  }

  public static PipelineOperationStatus handlePipelineTemplateInvocation(String username, PipelineTemplateInvocation pipelineTemplateInvocation, PipelineTemplateDescription pipelineTemplateDescription) {
    return new PipelineTemplateInvocationHandler(username, pipelineTemplateInvocation, pipelineTemplateDescription).handlePipelineInvocation();
  }

  public static PipelineTemplateInvocation getPipelineInvocationTemplate(SpDataStream dataStream, PipelineTemplateDescription pipelineTemplateDescription) {
    return new PipelineTemplateInvocationGenerator(dataStream, pipelineTemplateDescription).generateInvocation();
  }

  public static String getRuntimeInfo(SpDataStream spDataStream) throws SpRuntimeException {
    return PipelineElementRuntimeInfoFetcher.INSTANCE.getCurrentData(spDataStream);
  }
}
