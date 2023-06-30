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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.dataexplorer.DataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.DataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.manager.pipeline.PipelineCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineCanvasMetadataCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.storage.api.IDashboardStorage;
import org.apache.streampipes.storage.api.IDashboardWidgetStorage;
import org.apache.streampipes.storage.api.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResetManagement {
  // This class should be moved into another package. I moved it here because I got a cyclic maven
  // dependency between this package and streampipes-pipeline-management
  // See in issue [STREAMPIPES-405]

  private static final Logger logger = LoggerFactory.getLogger(ResetManagement.class);

  /**
   * Remove all configurations for this user. This includes:
   * [pipeline assembly cache, pipelines, adapters, files]
   *
   * @param username
   */
  public static void reset(String username) {
    logger.info("Start resetting the system");

    // Set hide tutorial to false for user
    UserResourceManager.setHideTutorial(username, true);

    // Clear pipeline assembly Cache
    PipelineCacheManager.removeCachedPipeline(username);
    PipelineCanvasMetadataCacheManager.removeCanvasMetadataFromCache(username);

    // Stop and delete all pipelines
    List<Pipeline> allPipelines = PipelineManager.getAllPipelines();
    allPipelines.forEach(pipeline -> {
      PipelineManager.stopPipeline(pipeline.getPipelineId(), true);
      PipelineManager.deletePipeline(pipeline.getPipelineId());
    });

    // Stop and delete all adapters
    AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

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

    // Stop and delete all files
    List<FileMetadata> allFiles = FileManager.getAllFiles();
    allFiles.forEach(fileMetadata -> {
      FileManager.deleteFile(fileMetadata.getFileId());
    });

    // Remove all data in data lake
    IDataExplorerSchemaManagement dataLakeMeasureManagement = new DataExplorerSchemaManagement();
    DataExplorerQueryManagement dataExplorerQueryManagement =
        new DataExplorerQueryManagement(dataLakeMeasureManagement);
    List<DataLakeMeasure> allMeasurements = dataLakeMeasureManagement.getAllMeasurements();
    allMeasurements.forEach(measurement -> {
      boolean isSuccessDataLake = dataExplorerQueryManagement.deleteData(measurement.getMeasureName());

      if (isSuccessDataLake) {
        dataLakeMeasureManagement.deleteMeasurementByName(measurement.getMeasureName());
      }
    });

    // Remove all data views widgets
    IDataExplorerWidgetStorage widgetStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerWidgetStorage();
    widgetStorage.getAllDataExplorerWidgets().forEach(widget -> {
      widgetStorage.deleteDataExplorerWidget(widget.getId());
    });

    // Remove all data views
    IDashboardStorage dataLakeDashboardStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerDashboardStorage();
    dataLakeDashboardStorage.getAllDashboards().forEach(dashboard -> {
      dataLakeDashboardStorage.deleteDashboard(dashboard.getCouchDbId());
    });

    // Remove all dashboard widgets
    IDashboardWidgetStorage dashobardWidgetStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore().getDashboardWidgetStorage();
    dashobardWidgetStorage.getAllDashboardWidgets().forEach(widget -> {
      dashobardWidgetStorage.deleteDashboardWidget(widget.getId());
    });

    // Remove all dashboards
    IDashboardStorage dashboardStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getDashboardStorage();
    dashboardStorage.getAllDashboards().forEach(dashboard -> {
      dashboardStorage.deleteDashboard(dashboard.getCouchDbId());
    });

    logger.info("Resetting the system was completed");
  }
}
