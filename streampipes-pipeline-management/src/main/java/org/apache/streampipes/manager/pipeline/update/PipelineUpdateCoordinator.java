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

import org.apache.streampipes.commons.prometheus.pipelines.PipelinesStats;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.manager.matching.v2.pipeline.MeasurementChangeValidationStep;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PipelineUpdateCoordinator {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineUpdateCoordinator.class);
  private static final PipelinesStats PIPELINES_STATS = new PipelinesStats();

  private final ExtensionServiceRequestManager requestManager;

  public PipelineUpdateCoordinator(ExtensionServiceRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void updatePipelines(SpDataStream dataStream) {
    updatePipelines(
        dataStream.getElementId(),
        dataStream.getName(),
        dataStream.getEventSchema(),
        "Data stream"
    );
  }

  public void updatePipelines(AdapterDescription adapterDescription) {
    updatePipelines(
        adapterDescription.getCorrespondingDataStreamElementId(),
        adapterDescription.getName(),
        adapterDescription.getEventSchema(),
        "Adapter"
    );
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(SpDataStream dataStream) {
    return checkPipelineMigrations(
        dataStream.getElementId(),
        dataStream.getName(),
        dataStream.getEventSchema()
    );
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(AdapterDescription adapterDescription) {
    return checkPipelineMigrations(
        adapterDescription.getCorrespondingDataStreamElementId(),
        adapterDescription.getName(),
        adapterDescription.getEventSchema()
    );
  }

  private void updatePipelines(String affectedElementId,
                               String updatedStreamName,
                               EventSchema updatedEventSchema,
                               String notificationType) {
    var affectedPipelines = PipelineManager.getPipelinesContainingElements(affectedElementId);

    affectedPipelines.forEach(pipeline -> {
      var shouldRestartPipeline = pipeline.isRunning();
      if (shouldRestartPipeline) {
        new PipelineExecutor(pipeline, requestManager).stopPipeline(true);
      }

      var storedPipeline = PipelineManager.getPipeline(pipeline.getPipelineId());
      var updatedPipeline = updatePipeline(storedPipeline, affectedElementId, updatedStreamName, updatedEventSchema);

      try {
        var verificationHandler = new PipelineVerificationHandlerV2(updatedPipeline, requestManager);
        var modificationMessage = verificationHandler.verifyPipeline();
        var updateInfo = makeUpdateInfo(modificationMessage, updatedPipeline);
        var modifiedPipeline = verificationHandler.makeModifiedPipeline(modificationMessage).pipeline();
        var canAutoMigrate = canAutoMigrate(modificationMessage);

        if (!canAutoMigrate) {
          modifiedPipeline.setHealthStatus(
              requiresMeasurementUpdate(modificationMessage)
                  ? PipelineHealthStatus.HANDLE_MEASUREMENT_UPDATE
                  : PipelineHealthStatus.REQUIRES_ATTENTION
          );
          PIPELINES_STATS.updatePipelineHealthState(
              modifiedPipeline.getElementId(),
              modifiedPipeline.getName(),
              modifiedPipeline.getHealthStatus().toString()
          );
          modifiedPipeline.setPipelineNotifications(toNotification(updateInfo, notificationType));
          modifiedPipeline.setValid(false);
        }

        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updateElement(modifiedPipeline);

        if (shouldRestartPipeline && canAutoMigrate) {
          new PipelineExecutor(PipelineManager.getPipeline(pipeline.getPipelineId()), requestManager).startPipeline();
        }
      } catch (Exception e) {
        LOG.error("Could not update pipeline {}", updatedPipeline.getName(), e);
      }
    });
  }

  private List<PipelineUpdateInfo> checkPipelineMigrations(String affectedElementId,
                                                           String updatedStreamName,
                                                           EventSchema updatedEventSchema) {
    var affectedPipelines = PipelineManager.getPipelinesContainingElements(affectedElementId);
    var updateInfos = new ArrayList<PipelineUpdateInfo>();

    affectedPipelines.forEach(pipeline -> {
      var updatedPipeline = updatePipeline(pipeline, affectedElementId, updatedStreamName, updatedEventSchema);
      try {
        var modificationMessage = new PipelineVerificationHandlerV2(updatedPipeline, requestManager).verifyPipeline();
        var updateInfo = makeUpdateInfo(modificationMessage, updatedPipeline);
        updateInfos.add(updateInfo);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    return updateInfos;
  }

  private Pipeline updatePipeline(Pipeline pipeline,
                                  String affectedElementId,
                                  String updatedStreamName,
                                  EventSchema updatedEventSchema) {
    var updatedStreams = pipeline
        .getStreams()
        .stream()
        .peek(stream -> {
          if (stream.getElementId().equals(affectedElementId)) {
            stream.setEventSchema(updatedEventSchema);
            stream.setName(updatedStreamName);
          }
        })
        .toList();

    pipeline.setStreams(updatedStreams);
    return pipeline;
  }

  private PipelineUpdateInfo makeUpdateInfo(PipelineModificationMessage modificationMessage,
                                            Pipeline pipeline) {
    var updateInfo = new PipelineUpdateInfo();
    updateInfo.setPipelineId(pipeline.getPipelineId());
    updateInfo.setPipelineName(pipeline.getName());
    updateInfo.setCanAutoMigrate(canAutoMigrate(modificationMessage));
    updateInfo.setValidationInfos(extractModificationWarnings(pipeline, modificationMessage));
    return updateInfo;
  }

  private boolean canAutoMigrate(PipelineModificationMessage modificationMessage) {
    return modificationMessage
        .getPipelineModifications()
        .stream()
        .allMatch(modification -> modification.isPipelineElementValid()
            && modification.getValidationInfos().isEmpty());
  }

  private boolean requiresMeasurementUpdate(PipelineModificationMessage modificationMessage) {
    return modificationMessage
        .getPipelineModifications()
        .stream()
        .flatMap(modification -> modification.getValidationInfos().stream())
        .anyMatch(validationInfo -> validationInfo.getMessage() != null
            && validationInfo.getMessage().startsWith(MeasurementChangeValidationStep.MEASUREMENT_UPDATE_REQUIRED));
  }

  private List<String> toNotification(PipelineUpdateInfo updateInfo,
                                      String updateType) {
    var notifications = new ArrayList<String>();
    updateInfo.getValidationInfos().forEach((pipelineElementName, validationInfos) -> {
      var message = validationInfos
          .stream()
          .map(PipelineElementValidationInfo::getMessage)
          .toList()
          .toString();
      notifications.add(String.format("%s modification: %s: %s", updateType, pipelineElementName, message));
    });
    return notifications;
  }

  private Map<String, List<PipelineElementValidationInfo>> extractModificationWarnings(
      Pipeline pipeline,
      PipelineModificationMessage modificationMessage) {
    var infos = new HashMap<String, List<PipelineElementValidationInfo>>();
    modificationMessage
        .getPipelineModifications()
        .stream()
        .filter(modification -> !modification.getValidationInfos().isEmpty())
        .forEach(modification -> infos.put(
            getPipelineElementName(pipeline, modification.getElementId()),
            modification.getValidationInfos()
        ));

    return infos;
  }

  private String getPipelineElementName(Pipeline pipeline,
                                        String elementId) {
    return Stream
        .concat(pipeline.getSepas().stream(), pipeline.getActions().stream())
        .filter(pipelineElement -> pipelineElement.getElementId().equals(elementId))
        .findFirst()
        .map(NamedStreamPipesEntity::getName)
        .orElse(elementId);
  }
}
