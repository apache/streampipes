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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AdapterUpdateManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterUpdateManagement.class);

  private final AdapterMasterManagement adapterMasterManagement;
  private final AdapterResourceManager adapterResourceManager;
  private final DataStreamResourceManager dataStreamResourceManager;

  public AdapterUpdateManagement(AdapterMasterManagement adapterMasterManagement) {
    this.adapterMasterManagement = adapterMasterManagement;
    this.adapterResourceManager = new SpResourceManager().manageAdapters();
    this.dataStreamResourceManager = new SpResourceManager().manageDataStreams();
  }

  public void updateAdapter(AdapterDescription ad)
      throws AdapterException {
    // update adapter in database
    this.adapterResourceManager.encryptAndUpdate(ad);
    boolean shouldRestart = ad.isRunning();

    if (ad.isRunning()) {
      this.adapterMasterManagement.stopStreamAdapter(ad.getElementId());
    }

    // update data source in database
    this.updateDataSource(ad);

    // update pipelines
    var affectedPipelines = PipelineManager.getPipelinesContainingElements(ad.getCorrespondingDataStreamElementId());

    affectedPipelines.forEach(p -> {
      var shouldRestartPipeline = p.isRunning();
      if (shouldRestartPipeline) {
        new PipelineExecutor(p).stopPipeline(true);
      }
      var storedPipeline = PipelineManager.getPipeline(p.getPipelineId());
      var pipeline = applyUpdatedDataStream(storedPipeline, ad);
      try {
        var modificationMessage = new PipelineVerificationHandlerV2(pipeline).verifyPipeline();
        var updateInfo = makeUpdateInfo(modificationMessage, pipeline);
        var modifiedPipeline = new PipelineVerificationHandlerV2(pipeline).makeModifiedPipeline();
        var canAutoMigrate = canAutoMigrate(modificationMessage);
        if (!canAutoMigrate) {
          modifiedPipeline.setHealthStatus(PipelineHealthStatus.REQUIRES_ATTENTION);
          modifiedPipeline.setPipelineNotifications(toNotification(updateInfo));
          modifiedPipeline.setValid(false);
        }
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updateElement(modifiedPipeline);
        if (shouldRestartPipeline && canAutoMigrate) {
          new PipelineExecutor(PipelineManager.getPipeline(p.getPipelineId())).startPipeline();
        }
      } catch (Exception e) {
        LOG.error("Could not update pipeline {}", pipeline.getName(), e);
      }
    });

    if (shouldRestart) {
      this.adapterMasterManagement.startStreamAdapter(ad.getElementId());
    }
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(AdapterDescription adapterDescription) {
    var affectedPipelines = PipelineManager
        .getPipelinesContainingElements(adapterDescription.getCorrespondingDataStreamElementId());
    var updateInfos = new ArrayList<PipelineUpdateInfo>();

    affectedPipelines.forEach(pipeline -> {
      var updatedPipeline = applyUpdatedDataStream(pipeline, adapterDescription);
      try {
        var modificationMessage = new PipelineVerificationHandlerV2(updatedPipeline).verifyPipeline();
        var updateInfo = makeUpdateInfo(modificationMessage, updatedPipeline);
        updateInfos.add(updateInfo);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    return updateInfos;
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
        .allMatch(m -> m.isPipelineElementValid() && m.getValidationInfos().isEmpty());
  }

  private List<String> toNotification(PipelineUpdateInfo updateInfo) {
    var notifications = new ArrayList<String>();
    updateInfo.getValidationInfos().keySet().forEach((k) -> {
      var msg =  updateInfo
          .getValidationInfos()
          .get(k)
          .stream()
          .map(PipelineElementValidationInfo::getMessage)
          .toList()
          .toString();
      notifications.add(String.format("Adapter modification: %s: %s", k, msg));
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
        .filter(v -> !v.getValidationInfos().isEmpty())
        .forEach(m -> infos.put(getPipelineElementName(pipeline, m.getElementId()), m.getValidationInfos()));

    return infos;
  }

  private String getPipelineElementName(Pipeline pipeline,
                                        String elementId) {
    return Stream
        .concat(pipeline.getSepas().stream(), pipeline.getActions().stream())
        .filter(p -> p.getElementId().equals(elementId))
        .findFirst()
        .map(NamedStreamPipesEntity::getName)
        .orElse(elementId);
  }

  private Pipeline applyUpdatedDataStream(Pipeline originalPipeline,
                                          AdapterDescription updatedAdapter) {
    var updatedStreams = originalPipeline
        .getStreams()
        .stream()
        .peek(s -> {
          if (s.getElementId().equals(updatedAdapter.getCorrespondingDataStreamElementId())) {
            s.setEventSchema(updatedAdapter.getEventSchema());
          }
        })
        .toList();

    originalPipeline.setStreams(updatedStreams);

    return originalPipeline;
  }

  private void updateDataSource(AdapterDescription ad) {
    // get data source
    SpDataStream dataStream = this.dataStreamResourceManager.find(ad.getCorrespondingDataStreamElementId());

    SourcesManagement.updateDataStream(ad, dataStream);

    // Update data source in database
    this.dataStreamResourceManager.update(dataStream);
  }
}
