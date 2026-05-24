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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.manager.matching.v2.pipeline.MeasurementChangeValidationStep;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.ChartSchemaUpdateInfo;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.model.pipeline.PipelineModificationResult;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PipelineUpdateCoordinatorTest {

  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";

  @Test
  void updatePipelines_ShouldRestartRunningPipelinesForDataStreamUpdates() {
    var requestManager = mock(ExtensionServiceRequestManager.class);
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var coordinator = new PipelineUpdateCoordinator(requestManager, chartSchemaUpdateCoordinator);
    var dataStream = makeDataStream("stream-1", "Updated stream");
    var affectedPipeline = makePipeline("pipeline-1", "Pipeline", true, "stream-1", "Old stream");
    var storedPipeline = makePipeline("pipeline-1", "Pipeline", true, "stream-1", "Old stream");
    var modifiedPipeline = makePipeline("pipeline-1", "Pipeline", true, "stream-1", "Updated stream");

    var modificationMessage = new PipelineModificationMessage(List.of(validModification("sepa-1")));
    var noSqlStorage = mock(INoSqlStorage.class);
    var pipelineStorage = mock(IPipelineStorage.class);
    var verifiedPipelines = new ArrayList<Pipeline>();
    when(noSqlStorage.getPipelineStorageAPI()).thenReturn(pipelineStorage);

    try (MockedStatic<PipelineManager> pipelineManager = mockStatic(PipelineManager.class);
         MockedConstruction<PipelineVerificationHandlerV2> verificationHandlerConstruction =
             mockConstruction(PipelineVerificationHandlerV2.class, (mock, context) -> {
               verifiedPipelines.add((Pipeline) context.arguments().get(0));
               when(mock.verifyPipeline()).thenReturn(modificationMessage);
               when(mock.makeModifiedPipeline(modificationMessage))
                   .thenReturn(new PipelineModificationResult(modifiedPipeline, List.of()));
             });
         MockedConstruction<CouchDbStorageManager> storageManagerConstruction =
             mockConstruction(CouchDbStorageManager.class, (mock, context) ->
                 when(mock.getPipelineStorageAPI()).thenReturn(pipelineStorage));
         MockedConstruction<PipelineExecutor> executorConstruction =
             mockConstruction(PipelineExecutor.class)) {

      pipelineManager.when(() -> PipelineManager.getPipelinesContainingElements("stream-1"))
          .thenReturn(List.of(affectedPipeline));
      pipelineManager.when(() -> PipelineManager.getPipeline("pipeline-1"))
          .thenReturn(storedPipeline, modifiedPipeline);

      coordinator.updatePipelines(dataStream);

      assertEquals(1, verificationHandlerConstruction.constructed().size());
      var updatedPipeline = verifiedPipelines.get(0);
      assertEquals("Updated stream", updatedPipeline.getStreams().get(0).getName());
      assertSame(dataStream.getEventSchema(), updatedPipeline.getStreams().get(0).getEventSchema());

      assertEquals(2, executorConstruction.constructed().size());
      verify(executorConstruction.constructed().get(0)).stopPipeline(true);
      verify(executorConstruction.constructed().get(1)).startPipeline();
      assertEquals(1, storageManagerConstruction.constructed().size());
      verify(pipelineStorage).updateElement(modifiedPipeline);
      verifyNoInteractions(chartSchemaUpdateCoordinator);
    }
  }

  @Test
  void updatePipelines_ShouldMarkPipelinesRequiringAttentionForAdapterUpdates() {
    var requestManager = mock(ExtensionServiceRequestManager.class);
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var coordinator = new PipelineUpdateCoordinator(requestManager, chartSchemaUpdateCoordinator);
    var adapterDescription = makeAdapter("stream-1", "Updated adapter");
    var storedPipeline = makePipeline("pipeline-1", "Pipeline", false, "stream-1", "Old stream");
    var modifiedPipeline = makePipeline("pipeline-1", "Pipeline", false, "stream-1", "Updated adapter");
    modifiedPipeline.setSepas(List.of(makeSepa("sepa-1", "Processor")));

    var warning = PipelineElementValidationInfo.error("Schema mismatch");
    var modificationMessage = new PipelineModificationMessage(List.of(invalidModification("sepa-1", warning)));
    var noSqlStorage = mock(INoSqlStorage.class);
    var pipelineStorage = mock(IPipelineStorage.class);
    var verifiedPipelines = new ArrayList<Pipeline>();
    when(noSqlStorage.getPipelineStorageAPI()).thenReturn(pipelineStorage);

    try (MockedStatic<PipelineManager> pipelineManager = mockStatic(PipelineManager.class);
         MockedConstruction<PipelineVerificationHandlerV2> verificationHandlerConstruction =
             mockConstruction(PipelineVerificationHandlerV2.class, (mock, context) -> {
               verifiedPipelines.add((Pipeline) context.arguments().get(0));
               when(mock.verifyPipeline()).thenReturn(modificationMessage);
               when(mock.makeModifiedPipeline(modificationMessage))
                   .thenReturn(new PipelineModificationResult(modifiedPipeline, List.of()));
             });
         MockedConstruction<CouchDbStorageManager> storageManagerConstruction =
             mockConstruction(CouchDbStorageManager.class, (mock, context) ->
                 when(mock.getPipelineStorageAPI()).thenReturn(pipelineStorage));
         MockedConstruction<PipelineExecutor> executorConstruction =
             mockConstruction(PipelineExecutor.class)) {

      pipelineManager.when(() -> PipelineManager.getPipelinesContainingElements("stream-1"))
          .thenReturn(List.of(storedPipeline));
      pipelineManager.when(() -> PipelineManager.getPipeline("pipeline-1"))
          .thenReturn(storedPipeline);

      coordinator.updatePipelines(adapterDescription);

      assertEquals(1, verificationHandlerConstruction.constructed().size());
      var updatedPipeline = verifiedPipelines.get(0);
      assertEquals("Updated adapter", updatedPipeline.getStreams().get(0).getName());
      assertSame(adapterDescription.getEventSchema(), updatedPipeline.getStreams().get(0).getEventSchema());

      assertEquals(0, executorConstruction.constructed().size());
      assertEquals(1, storageManagerConstruction.constructed().size());
      var pipelineCaptor = ArgumentCaptor.forClass(Pipeline.class);
      verify(pipelineStorage).updateElement(pipelineCaptor.capture());
      assertEquals(PipelineHealthStatus.REQUIRES_ATTENTION, pipelineCaptor.getValue().getHealthStatus());
      assertFalse(pipelineCaptor.getValue().isValid());
      assertEquals(List.of("Adapter modification: Processor: [Schema mismatch]"),
          pipelineCaptor.getValue().getPipelineNotifications());
    }
  }

  @Test
  void updatePipelines_ShouldMarkPipelineRequiringAttentionForCriticalMeasurementFieldChange() {
    var requestManager = mock(ExtensionServiceRequestManager.class);
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var coordinator = new PipelineUpdateCoordinator(requestManager, chartSchemaUpdateCoordinator);
    var adapterDescription = makeAdapter("stream-1", "Updated adapter");
    adapterDescription.getDataStream().setEventSchema(makeSchema(makeMeasurementProperty("temperature", XSD.STRING)));

    var storedPipeline = makePipeline("pipeline-1", "Pipeline", true, "stream-1", "Old stream");
    storedPipeline.getStreams().get(0).setEventSchema(makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)));
    storedPipeline.setActions(List.of(makeDataLakeSink()));

    var modifiedPipeline = makePipeline("pipeline-1", "Pipeline", true, "stream-1", "Updated adapter");
    var measurementUpdateInfo = PipelineElementValidationInfo.info(
        measurementUpdateRequiredMessage());
    var modificationMessage = new PipelineModificationMessage(List.of(validModification("sepa-1", measurementUpdateInfo)));
    var pipelineStorage = mock(IPipelineStorage.class);

    try (MockedStatic<PipelineManager> pipelineManager = mockStatic(PipelineManager.class);
         MockedConstruction<PipelineVerificationHandlerV2> verificationHandlerConstruction =
             mockConstruction(PipelineVerificationHandlerV2.class, (mock, context) -> {
               when(mock.verifyPipeline()).thenReturn(modificationMessage);
               when(mock.makeModifiedPipeline(modificationMessage))
                   .thenReturn(new PipelineModificationResult(modifiedPipeline, List.of()));
             });
         MockedConstruction<CouchDbStorageManager> storageManagerConstruction =
             mockConstruction(CouchDbStorageManager.class, (mock, context) ->
                 when(mock.getPipelineStorageAPI()).thenReturn(pipelineStorage));
         MockedConstruction<PipelineExecutor> executorConstruction =
             mockConstruction(PipelineExecutor.class)) {

      pipelineManager.when(() -> PipelineManager.getPipelinesContainingElements("stream-1"))
          .thenReturn(List.of(storedPipeline));
      pipelineManager.when(() -> PipelineManager.getPipeline("pipeline-1"))
          .thenReturn(storedPipeline);

      coordinator.updatePipelines(adapterDescription);

      assertEquals(1, verificationHandlerConstruction.constructed().size());
      assertEquals(1, storageManagerConstruction.constructed().size());

      var pipelineCaptor = ArgumentCaptor.forClass(Pipeline.class);
      verify(pipelineStorage).updateElement(pipelineCaptor.capture());
      assertEquals(PipelineHealthStatus.HANDLE_MEASUREMENT_UPDATE, pipelineCaptor.getValue().getHealthStatus());
      assertFalse(pipelineCaptor.getValue().isValid());

      assertEquals(1, executorConstruction.constructed().size());
      verify(executorConstruction.constructed().get(0)).stopPipeline(true);
    }
  }

  @Test
  void checkPipelineMigrations_ShouldUseUpdatedDataStreamValues() {
    var requestManager = mock(ExtensionServiceRequestManager.class);
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var coordinator = new PipelineUpdateCoordinator(requestManager, chartSchemaUpdateCoordinator);
    var dataStream = makeDataStream("stream-1", "Updated stream");
    var pipeline = makePipeline("pipeline-1", "Pipeline", false, "stream-1", "Old stream");
    var modificationMessage = new PipelineModificationMessage(List.of(validModification("sepa-1")));
    var verifiedPipelines = new ArrayList<Pipeline>();
    var chartUpdateInfo = new ChartSchemaUpdateInfo();

    try (MockedStatic<PipelineManager> pipelineManager = mockStatic(PipelineManager.class);
         MockedConstruction<PipelineVerificationHandlerV2> verificationHandlerConstruction =
             mockConstruction(PipelineVerificationHandlerV2.class, (mock, context) -> {
               verifiedPipelines.add((Pipeline) context.arguments().get(0));
               when(mock.verifyPipeline()).thenReturn(modificationMessage);
               when(mock.makeModifiedPipeline(modificationMessage))
                   .thenReturn(new PipelineModificationResult((Pipeline) context.arguments().get(0), List.of()));
             })) {

      pipelineManager.when(() -> PipelineManager.getPipelinesContainingElements("stream-1"))
          .thenReturn(List.of(pipeline));
      when(chartSchemaUpdateCoordinator.checkChartMigrations(pipeline, dataStream.getEventSchema()))
          .thenReturn(List.of(chartUpdateInfo));

      var result = coordinator.checkPipelineMigrations(dataStream);

      assertEquals(1, result.size());
      assertEquals("pipeline-1", result.get(0).getPipelineId());
      assertEquals("Pipeline", result.get(0).getPipelineName());
      assertTrue(result.get(0).isCanAutoMigrate());
      assertEquals(List.of(chartUpdateInfo), result.get(0).getChartSchemaUpdateInfos());

      assertEquals(1, verificationHandlerConstruction.constructed().size());
      var updatedPipeline = verifiedPipelines.get(0);
      assertEquals("Updated stream", updatedPipeline.getStreams().get(0).getName());
      assertSame(dataStream.getEventSchema(), updatedPipeline.getStreams().get(0).getEventSchema());
    }
  }

  @Test
  void checkPipelineMigrations_ShouldReportWarningsForAdapterUpdates() {
    var requestManager = mock(ExtensionServiceRequestManager.class);
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var coordinator = new PipelineUpdateCoordinator(requestManager, chartSchemaUpdateCoordinator);
    var adapterDescription = makeAdapter("stream-1", "Updated adapter");
    var pipeline = makePipeline("pipeline-1", "Pipeline", false, "stream-1", "Old stream");
    pipeline.setSepas(List.of(makeSepa("sepa-1", "Processor")));
    var warning = PipelineElementValidationInfo.error("Schema mismatch");
    var modificationMessage = new PipelineModificationMessage(List.of(invalidModification("sepa-1", warning)));
    var verifiedPipelines = new ArrayList<Pipeline>();

    try (MockedStatic<PipelineManager> pipelineManager = mockStatic(PipelineManager.class);
         MockedConstruction<PipelineVerificationHandlerV2> verificationHandlerConstruction =
             mockConstruction(PipelineVerificationHandlerV2.class, (mock, context) -> {
               verifiedPipelines.add((Pipeline) context.arguments().get(0));
               when(mock.verifyPipeline()).thenReturn(modificationMessage);
               when(mock.makeModifiedPipeline(modificationMessage))
                   .thenReturn(new PipelineModificationResult((Pipeline) context.arguments().get(0), List.of()));
             })) {

      pipelineManager.when(() -> PipelineManager.getPipelinesContainingElements("stream-1"))
          .thenReturn(List.of(pipeline));

      var result = coordinator.checkPipelineMigrations(adapterDescription);

      assertEquals(1, result.size());
      PipelineUpdateInfo updateInfo = result.get(0);
      assertFalse(updateInfo.isCanAutoMigrate());
      assertEquals(1, updateInfo.getValidationInfos().size());
      assertEquals(List.of(warning), updateInfo.getValidationInfos().get("Processor"));

      assertEquals(1, verificationHandlerConstruction.constructed().size());
      var updatedPipeline = verifiedPipelines.get(0);
      assertEquals("Updated adapter", updatedPipeline.getStreams().get(0).getName());
      assertSame(adapterDescription.getEventSchema(), updatedPipeline.getStreams().get(0).getEventSchema());
    }
  }

  @Test
  void checkPipelineMigrations_ShouldDisableAutoMigrationForCriticalMeasurementFieldChange() {
    var requestManager = mock(ExtensionServiceRequestManager.class);
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var coordinator = new PipelineUpdateCoordinator(requestManager, chartSchemaUpdateCoordinator);
    var adapterDescription = makeAdapter("stream-1", "Updated adapter");
    adapterDescription.getDataStream().setEventSchema(makeSchema(makeMeasurementProperty("temperature", XSD.STRING)));

    var pipeline = makePipeline("pipeline-1", "Pipeline", false, "stream-1", "Old stream");
    pipeline.getStreams().get(0).setEventSchema(makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)));
    pipeline.setActions(List.of(makeDataLakeSink()));

    var measurementUpdateInfo = PipelineElementValidationInfo.info(
        measurementUpdateRequiredMessage());
    var modificationMessage = new PipelineModificationMessage(List.of(validModification("sepa-1", measurementUpdateInfo)));

    try (MockedStatic<PipelineManager> pipelineManager = mockStatic(PipelineManager.class);
         MockedConstruction<PipelineVerificationHandlerV2> verificationHandlerConstruction =
             mockConstruction(PipelineVerificationHandlerV2.class, (mock, context) -> {
               when(mock.verifyPipeline()).thenReturn(modificationMessage);
               when(mock.makeModifiedPipeline(modificationMessage))
                   .thenReturn(new PipelineModificationResult((Pipeline) context.arguments().get(0), List.of()));
             })) {

      pipelineManager.when(() -> PipelineManager.getPipelinesContainingElements("stream-1"))
          .thenReturn(List.of(pipeline));

      var result = coordinator.checkPipelineMigrations(adapterDescription);

      assertEquals(1, verificationHandlerConstruction.constructed().size());
      assertEquals(1, result.size());
      assertFalse(result.get(0).isCanAutoMigrate());
    }
  }

  private SpDataStream makeDataStream(String elementId, String name) {
    var dataStream = new SpDataStream();
    dataStream.setElementId(elementId);
    dataStream.setName(name);
    dataStream.setEventSchema(new EventSchema());
    return dataStream;
  }

  private AdapterDescription makeAdapter(String correspondingDataStreamElementId, String name) {
    var adapterDescription = new AdapterDescription();
    adapterDescription.setCorrespondingDataStreamElementId(correspondingDataStreamElementId);
    adapterDescription.setName(name);
    adapterDescription.getDataStream().setEventSchema(new EventSchema());
    return adapterDescription;
  }

  private Pipeline makePipeline(String pipelineId,
                                String name,
                                boolean running,
                                String streamElementId,
                                String streamName) {
    var pipeline = new Pipeline();
    pipeline.setPipelineId(pipelineId);
    pipeline.setName(name);
    pipeline.setRunning(running);
    pipeline.setStreams(List.of(makeDataStream(streamElementId, streamName)));
    pipeline.setSepas(List.of(makeSepa("sepa-1", "Processor")));
    return pipeline;
  }

  private DataSinkInvocation makeDataLakeSink() {
    var sink = new DataSinkInvocation();
    sink.setAppId(DATA_LAKE_SINK_APP_ID);
    return sink;
  }

  private EventSchema makeSchema(EventPropertyPrimitive... eventProperties) {
    return new EventSchema(List.of(eventProperties));
  }

  private EventPropertyPrimitive makeMeasurementProperty(String runtimeName,
                                                        URI runtimeType) {
    var property = new EventPropertyPrimitive(runtimeType.toString(), runtimeName, "", "");
    property.setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());
    return property;
  }

  private String measurementUpdateRequiredMessage() {
    return MeasurementChangeValidationStep.MEASUREMENT_UPDATE_REQUIRED
        + ": temperature (" + XSD.INTEGER + " -> " + XSD.STRING + ")";
  }

  private DataProcessorInvocation makeSepa(String elementId, String name) {
    var sepa = new DataProcessorInvocation();
    sepa.setElementId(elementId);
    sepa.setName(name);
    return sepa;
  }

  private PipelineModification validModification(String elementId) {
    var modification = new PipelineModification();
    modification.setElementId(elementId);
    modification.setPipelineElementValid(true);
    return modification;
  }

  private PipelineModification validModification(String elementId,
                                                 PipelineElementValidationInfo validationInfo) {
    var modification = validModification(elementId);
    modification.setValidationInfos(List.of(validationInfo));
    return modification;
  }

  private PipelineModification invalidModification(String elementId,
                                                   PipelineElementValidationInfo validationInfo) {
    var modification = new PipelineModification();
    modification.setElementId(elementId);
    modification.setPipelineElementValid(false);
    modification.setValidationInfos(List.of(validationInfo));
    return modification;
  }
}
