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
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.manager.pipeline.PipelineCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineCanvasMetadataCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.storage.api.IGenericStorage;
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

  private static final Logger logger = LoggerFactory.getLogger(ResetManagement.class);

  /**
   * Remove all configurations for this user. This includes:
   * [pipeline assembly cache, pipelines, adapters, files, assets]
   *
   * @param username of the user to delte the resources
   */
  public static void reset(String username) {
    logger.info("Start resetting the system");

    setHideTutorialToFalse(username);

    clearPipelineAssemblyCache(username);

    stopAndDeleteAllPipelines();

    stopAndDeleteAllAdapters();

    deleteAllFiles();

    removeAllDataInDataLake();

    removeAllDataViewWidgets();

    removeAllDataViews();

    removeAllDashboardWidgets();

    removeAllDashboards();

    removeAllAssets(username);

    removeAllPipelineTemplates();

    clearGenericStorage();

    logger.info("Resetting the system was completed");
  }

  private static void setHideTutorialToFalse(String username) {
    UserResourceManager.setHideTutorial(username, true);
  }

  private static void clearPipelineAssemblyCache(String username) {
    PipelineCacheManager.removeCachedPipeline(username);
    PipelineCanvasMetadataCacheManager.removeCanvasMetadataFromCache(username);
  }

  private static void stopAndDeleteAllPipelines() {
    List<Pipeline> allPipelines = PipelineManager.getAllPipelines();
    allPipelines.forEach(pipeline -> {
      PipelineManager.stopPipeline(pipeline.getPipelineId(), true);
      PipelineManager.deletePipeline(pipeline.getPipelineId());
    });
  }

  private static void stopAndDeleteAllAdapters() {
    AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement(
        StorageDispatcher.INSTANCE.getNoSqlStore()
            .getAdapterInstanceStorage(),
        new SpResourceManager().manageAdapters(),
        new SpResourceManager().manageDataStreams(),
        AdapterMetricsManager.INSTANCE.getAdapterMetrics()
    );

    try {
      List<AdapterDescription> allAdapters = adapterMasterManagement.getAllAdapterInstances();
      allAdapters.forEach(adapterDescription -> {
        try {
          adapterMasterManagement.deleteAdapter(adapterDescription.getElementId());
        } catch (AdapterException e) {
          logger.error("Failed to delete adapter with id: " + adapterDescription.getElementId(), e);
        }
      });
    } catch (AdapterException e) {
      logger.error("Failed to load all adapter descriptions", e);
    }
  }

  private static void deleteAllFiles() {
    var fileManager = new FileManager();
    List<FileMetadata> allFiles = fileManager.getAllFiles();
    allFiles.forEach(fileMetadata -> fileManager.deleteFile(fileMetadata.getFileId()));
  }

  private static void removeAllDataInDataLake() {
    var dataLakeMeasureManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getSchemaManagement();
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

  private static void removeAllDataViewWidgets() {
    var widgetStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore()
            .getDataExplorerWidgetStorage();
    widgetStorage.findAll()
        .forEach(widget -> widgetStorage.deleteElementById(widget.getElementId()));
  }

  private static void removeAllDataViews() {
    var dataLakeDashboardStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore()
            .getDataExplorerDashboardStorage();
    dataLakeDashboardStorage.findAll()
        .forEach(dashboard -> dataLakeDashboardStorage.deleteElementById(dashboard.getElementId()));
  }

  private static void removeAllDashboardWidgets() {
    var dashboardWidgetStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore()
            .getDashboardWidgetStorage();
    dashboardWidgetStorage.findAll()
        .forEach(widget -> dashboardWidgetStorage.deleteElementById(widget.getElementId()));
  }

  private static void removeAllDashboards() {
    var dashboardStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
        .getDashboardStorage();
    dashboardStorage.findAll()
        .forEach(dashboard -> dashboardStorage.deleteElementById(dashboard.getElementId()));
  }

  private static void removeAllAssets(String username) {
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

  private static void removeAllPipelineTemplates() {
    var pipelineElementTemplateStorage = StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getPipelineElementTemplateStorage();

    pipelineElementTemplateStorage
        .findAll()
        .forEach(pipelineElementTemplateStorage::deleteElement);

  }

  private static void clearGenericStorage() {
    var appDocTypesToDelete = List.of(
        "asset-management",
        "asset-sites"
    );
    var genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();

    appDocTypesToDelete.forEach(docType -> {
      try {
        var allDocs = genericStorage.findAll(docType);
        for (var doc : allDocs) {
          genericStorage.delete(doc.get("_id").toString(), doc.get("_rev").toString());
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

  }
}
