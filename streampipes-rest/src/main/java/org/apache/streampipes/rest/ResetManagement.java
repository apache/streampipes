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

package org.apache.streampipes.rest;

import org.apache.streampipes.audit.events.adapter.AdapterAuditRecorder;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.manager.pipeline.PipelineCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineCanvasMetadataCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.configuration.SystemNotificationConfig;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResetManagement {
  // This class should be moved into another package. I moved it here because I got a cyclic maven
  // dependency between this package and streampipes-pipeline-management
  // See in issue [STREAMPIPES-405]

  private final IGenericStorage genericStorage;
  private final WorkerRestClient workerRestClient;
  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final ExtensionServiceRequestManager requestManager;
  private final ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator;
  private final PipelineManager pipelineManager;
  private final SpResourceManager resourceManager;
  private final IPipelineElementTemplateStorage pipelineElementTemplateStorage;

  public ResetManagement(WorkerRestClient workerRestClient,
                         IGenericStorage genericStorage,
                         IExtensionsServiceStorage extensionsServiceStorage,
                         ExtensionServiceRequestManager requestManager,
                         PipelineManager pipelineManager,
                         SpResourceManager resourceManager,
                         IPipelineElementTemplateStorage pipelineElementTemplateStorage,
                         ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator) {
    this.workerRestClient = workerRestClient;
    this.genericStorage = genericStorage;
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.requestManager = requestManager;
    this.pipelineManager = pipelineManager;
    this.resourceManager = resourceManager;
    this.pipelineElementTemplateStorage = pipelineElementTemplateStorage;
    this.chartSchemaUpdateCoordinator = chartSchemaUpdateCoordinator;
  }

  private static final Logger logger = LoggerFactory.getLogger(ResetManagement.class);

  /**
   * Remove all configurations for this user. This includes:
   * [pipeline assembly cache, pipelines, adapters, files, assets, system notification]
   *
   * @param username of the user to delte the resources
   */
  public void reset(String username) {
    logger.info("Start resetting the system");

    setHideTutorialToFalse(username);

    clearPipelineAssemblyCache(username);

    stopAndDeleteAllPipelines(requestManager);

    stopAndDeleteAllAdapters(workerRestClient, extensionsServiceStorage, requestManager);

    deleteAllFiles();

    removeAllDataInDataLake();

    removeAllDataViewWidgets();

    removeAllDataViews();

    removeAllAssets(username);

    removeAllPipelineTemplates();

    clearGenericStorage();

    disableSystemNotification();

    logger.info("Resetting the system was completed");
  }

  private void setHideTutorialToFalse(String username) {
    resourceManager.manageUsers().setHideTutorial(username, true);
  }

  private void clearPipelineAssemblyCache(String username) {
    PipelineCacheManager.removeCachedPipeline(username);
    PipelineCanvasMetadataCacheManager.removeCanvasMetadataFromCache(username);
  }

  private void stopAndDeleteAllPipelines(ExtensionServiceRequestManager requestManager) {
    List<Pipeline> allPipelines = pipelineManager.getAllPipelines();
    allPipelines.forEach(pipeline -> {
      pipelineManager.stopPipeline(pipeline.getPipelineId(), true, requestManager);
      pipelineManager.deletePipeline(pipeline.getPipelineId());
    });
  }

  private void stopAndDeleteAllAdapters(WorkerRestClient workerRestClient,
                                         IExtensionsServiceStorage extensionsServiceStorage,
                                         ExtensionServiceRequestManager requestManager) {
    AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement(
        resourceManager,
        AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
        workerRestClient,
        extensionsServiceStorage,
        requestManager,
        new AdapterAuditRecorder(resourceManager.getAuditService())
    );

    List<AdapterDescription> allAdapters = adapterMasterManagement.getAllAdapterInstances();
    allAdapters.forEach(adapterDescription -> {
      try {
        adapterMasterManagement.deleteAdapter(adapterDescription.getElementId());
      } catch (AdapterException e) {
        logger.error("Failed to delete adapter with id: " + adapterDescription.getElementId(), e);
      }
    });
  }

  private void deleteAllFiles() {
    var fileManager = new FileManager(
        resourceManager.getCoreConfigurationStorage(),
        resourceManager.getFileMetadataStorage()
    );
    List<FileMetadata> allFiles = fileManager.getAllFiles();
    allFiles.forEach(fileMetadata -> fileManager.deleteFile(fileMetadata.getFileId(), resourceManager.getResourceDeletionManager()));
  }

  private void removeAllDataInDataLake() {
    var datasetMetadataManagement = new DataExplorerDispatcher()
        .getSchemaManagement(
            chartSchemaUpdateCoordinator,
            resourceManager.managePermissions().getDb(),
            resourceManager.manageDataLakeMeasures().getDb(),
            resourceManager.getResourceDeletionManager());
    var dataExplorerQueryManagement = new DataExplorerDispatcher()
        .getDatasetServices(datasetMetadataManagement).administration();
    List<DatasetMetadata> allMeasurements = datasetMetadataManagement.getAllMeasurements();
    allMeasurements.forEach(measurement -> {
      boolean isSuccessDataLake = dataExplorerQueryManagement.deleteData(measurement.getMeasureName());

      if (isSuccessDataLake) {
        datasetMetadataManagement.deleteMeasurementByName(measurement.getMeasureName());
      }
    });
  }

  private void removeAllDataViewWidgets() {
    resourceManager.manageCharts().findAll()
                 .forEach(widget ->
                     resourceManager.manageCharts().delete(widget.getElementId()));
  }

  private void removeAllDataViews() {
    resourceManager.manageDashboards().findAll()
                            .forEach(dashboard ->
                                resourceManager.manageDashboards().delete(dashboard.getElementId()));
  }

  private void removeAllAssets(String username) {
    try {
      for (Map<String, Object> asset : genericStorage.findAll("asset-management")) {
        genericStorage.delete((String) asset.get("_id"), (String) asset.get("_rev"));
      }
    } catch (IOException e) {
      throw new SpRuntimeException("Could not delete assets of user %s".formatted(username));
    }
  }

  private void removeAllPipelineTemplates() {
    pipelineElementTemplateStorage
        .findAll()
        .forEach(pipelineElementTemplateStorage::deleteElement);

  }

  private void clearGenericStorage() {
    var appDocTypesToDelete = List.of(
        "asset-management",
        "asset-sites",
        "sp-labels"
    );

    appDocTypesToDelete.forEach(docType -> {
      try {
        var allDocs = genericStorage.findAll(docType);
        for (var doc : allDocs) {
          genericStorage.delete(
              doc.get("_id")
                 .toString(),
              doc.get("_rev")
                 .toString()
          );
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

  }

  /**
   * Switches the system notification off again, as on a fresh installation. The rest of the
   * general configuration stays untouched.
   */
  void disableSystemNotification() {
    var configStorage = resourceManager.getCoreConfigurationStorage();
    var config = configStorage.get();
    if (config != null && config.getGeneralConfig() != null) {
      config.getGeneralConfig().setSystemNotification(SystemNotificationConfig.disabled());
      configStorage.updateElement(config);
    }
  }
}
