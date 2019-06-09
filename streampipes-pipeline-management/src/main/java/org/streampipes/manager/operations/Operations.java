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

package org.streampipes.manager.operations;

import org.streampipes.commons.exceptions.NoSuitableSepasAvailableException;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.manager.endpoint.EndpointItemFetcher;
import org.streampipes.manager.execution.http.PipelineExecutor;
import org.streampipes.manager.execution.http.PipelineStorageService;
import org.streampipes.manager.matching.DataSetGroundingSelector;
import org.streampipes.manager.matching.PipelineVerificationHandler;
import org.streampipes.manager.recommender.ElementRecommender;
import org.streampipes.manager.remote.ContainerProvidedOptionsHandler;
import org.streampipes.manager.runtime.PipelineElementRuntimeInfoFetcher;
import org.streampipes.manager.template.PipelineTemplateGenerator;
import org.streampipes.manager.template.PipelineTemplateInvocationGenerator;
import org.streampipes.manager.template.PipelineTemplateInvocationHandler;
import org.streampipes.manager.topic.WildcardTopicGenerator;
import org.streampipes.manager.verification.extractor.TypeExtractor;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.model.client.endpoint.RdfEndpointItem;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.pipeline.*;
import org.streampipes.model.client.runtime.ContainerProvidedOptionsParameterRequest;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;

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

  public static Message verifyAndAddElement(String graphData, String username) throws SepaParseException {
    return verifyAndAddElement(graphData, username, false);
  }

  public static Message verifyAndAddElement(String graphData, String username, boolean publicElement) throws SepaParseException {
    return new TypeExtractor(graphData).getTypeVerifier().verifyAndAdd(username, publicElement);
  }

  public static Message verifyAndUpdateElement(String graphData, String username) throws SepaParseException {
    return new TypeExtractor(graphData).getTypeVerifier().verifyAndUpdate(username);
  }

  public static PipelineElementRecommendationMessage findRecommendedElements(String email, Pipeline partialPipeline) throws NoSuitableSepasAvailableException {
    return new ElementRecommender(email, partialPipeline).findRecommendedElements();
  }

  public static void storePipeline(Pipeline pipeline) {
    new PipelineStorageService(pipeline).addPipeline();
  }

  public static PipelineOperationStatus startPipeline(
          Pipeline pipeline) {
    return startPipeline(pipeline, true, true, false);
  }

  public static PipelineOperationStatus startPipeline(
          Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor) {
    return new PipelineExecutor(pipeline, visualize, storeStatus, monitor).startPipeline();
  }

  public static PipelineOperationStatus stopPipeline(
          Pipeline pipeline) {
    return stopPipeline(pipeline, true, true, false);
  }


  public static PipelineOperationStatus stopPipeline(
          Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor) {
    return new PipelineExecutor(pipeline, visualize, storeStatus, monitor).stopPipeline();
  }

  public static List<RdfEndpointItem> getEndpointUriContents(List<RdfEndpoint> endpoints) {
    return new EndpointItemFetcher(endpoints).getItems();
  }

  public static SpDataStream updateActualTopic(SpDataStream stream) {
    return new WildcardTopicGenerator(stream).computeActualTopic();
  }

  public static List<Option> fetchRemoteOptions(ContainerProvidedOptionsParameterRequest request) {
    return new ContainerProvidedOptionsHandler().fetchRemoteOptions(request);
  }

  public static List<PipelineTemplateDescription> getAllPipelineTemplates() {
    return new PipelineTemplateGenerator().makeExampleTemplates();
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
