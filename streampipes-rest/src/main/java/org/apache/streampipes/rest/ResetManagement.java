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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResetManagement {
  // This class should be moved into another package. I moved it here because I got a cyclic maven
  // dependency between this package and streampipes-pipeline-management
  // See in issue [STREAMPIPES-405]

  private final WorkerRestClient workerRestClient;
  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final ExtensionServiceRequestManager requestManager;
  private final ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator;
  private final PipelineManager pipelineManager;
  private final SpResourceManager resourceManager;

  public ResetManagement(WorkerRestClient workerRestClient,
                         IExtensionsServiceStorage extensionsServiceStorage,
                         ExtensionServiceRequestManager requestManager,
                         PipelineManager pipelineManager,
                         SpResourceManager resourceManager,
                         ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator) {
    this.workerRestClient = workerRestClient;
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.requestManager = requestManager;
    this.pipelineManager = pipelineManager;
    this.resourceManager = resourceManager;
    this.chartSchemaUpdateCoordinator = chartSchemaUpdateCoordinator;
  }

  private static final Logger logger = LoggerFactory.getLogger(ResetManagement.class);

  /**
   * Remove all configurations for this user. This includes:
   * [pipeline assembly cache, pipelines, adapters, files, assets]
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

    logger.info("Resetting the system was completed");
  }

  private void setHideTutorialToFalse(String username) {
    UserResourceManager.setHideTutorial(username, true);
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
        requestManager
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
    var fileManager = new FileManager();
    List<FileMetadata> allFiles = fileManager.getAllFiles();
    allFiles.forEach(fileMetadata -> fileManager.deleteFile(fileMetadata.getFileId()));
  }

  private void removeAllDataInDataLake() {
    var dataLakeMeasureManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getSchemaManagement(
            chartSchemaUpdateCoordinator,
            resourceManager.managePermissions().getDb(),
            resourceManager.manageDataLakeMeasures().getDb());
    var dataExplorerQueryManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getQueryManagement(dataLakeMeasureManagement);
    List<DataLakeMeasure> allMeasurements = dataLakeMeasureManagement.getAllMeasurements();
    allMeasurements.forEach(measurement -> {
      boolean isSuccessDataLake = dataExplorerQueryManagement.deleteData(measurement.getMeasureName());

      if (isSuccessDataLake) {
        dataLakeMeasureManagement.deleteMeasurementByName(measurement.getMeasureName());
      }
    });
  }

  private void removeAllDataViewWidgets() {
    resourceManager.manageCharts().findAll()
                 .forEach(widget ->
                     resourceManager.manageCharts().getDb().deleteElementById(widget.getElementId()));
  }

  private void removeAllDataViews() {
    resourceManager.manageDashboards().findAll()
                            .forEach(dashboard ->
                                resourceManager.manageDashboards().getDb().deleteElementById(dashboard.getElementId()));
  }

  private void removeAllAssets(String username) {
    IGenericStorage genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
                                                               .getGenericStorage();
    try {
      for (Map<String, Object> asset : genericStorage.findAll("asset-management")) {
        genericStorage.delete((String) asset.get("_id"), (String) asset.get("_rev"));
      }
    } catch (IOException e) {
      throw new SpRuntimeException("Could not delete assets of user %s".formatted(username));
    }
  }

  private void removeAllPipelineTemplates() {
    var pipelineElementTemplateStorage = StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getPipelineElementTemplateStorage();

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
    var genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
                                                   .getGenericStorage();

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
}
