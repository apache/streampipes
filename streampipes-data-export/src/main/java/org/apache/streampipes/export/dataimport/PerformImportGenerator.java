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

package org.apache.streampipes.export.dataimport;

import org.apache.streampipes.export.model.PermissionInfo;
import org.apache.streampipes.export.resolver.AdapterResolver;
import org.apache.streampipes.export.resolver.DashboardResolver;
import org.apache.streampipes.export.resolver.DashboardWidgetResolver;
import org.apache.streampipes.export.resolver.DataSourceResolver;
import org.apache.streampipes.export.resolver.DataViewResolver;
import org.apache.streampipes.export.resolver.DataViewWidgetResolver;
import org.apache.streampipes.export.resolver.FileResolver;
import org.apache.streampipes.export.resolver.MeasurementResolver;
import org.apache.streampipes.export.resolver.PipelineResolver;
import org.apache.streampipes.export.utils.ImportAdapterMigrationUtils;
import org.apache.streampipes.manager.file.FileHandler;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PerformImportGenerator extends ImportGenerator<Void> {

  private AssetExportConfiguration config;
  private INoSqlStorage storage;
  private Set<PermissionInfo> permissionsToStore = new HashSet<>();
  private String ownerSid;

  public PerformImportGenerator(AssetExportConfiguration config,
                                String ownerSid) {
    this.config = config;
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore();
    this.ownerSid = ownerSid;
  }

  @Override
  protected void handleAsset(Map<String, byte[]> previewFiles, String assetId) throws IOException {
    storage.getGenericStorage().create(asString(previewFiles.get(assetId)));
  }

  @Override
  protected void handleAdapter(String document, String adapterId) throws JsonProcessingException {
    if (shouldStore(adapterId, config.getAdapters())) {
      var convertedDoc = ImportAdapterMigrationUtils.checkAndPerformMigration(document);
      new AdapterResolver().writeDocument(convertedDoc, config.isOverrideBrokerSettings());
      permissionsToStore.add(new PermissionInfo(adapterId, AdapterDescription.class));
    }
  }

  @Override
  protected void handleDashboard(String document, String dashboardId) throws JsonProcessingException {
    if (shouldStore(dashboardId, config.getDashboards())) {
      new DashboardResolver().writeDocument(document);
      permissionsToStore.add(new PermissionInfo(dashboardId, DashboardModel.class));
    }
  }

  @Override
  protected void handleDataView(String document, String dataViewId) throws JsonProcessingException {
    if (shouldStore(dataViewId, config.getDataViews())) {
      new DataViewResolver().writeDocument(document);
      permissionsToStore.add(new PermissionInfo(dataViewId, DashboardModel.class));
    }
  }

  @Override
  protected void handleDataSource(String document, String dataSourceId) throws JsonProcessingException {
    if (shouldStore(dataSourceId, config.getDataSources())) {
      new DataSourceResolver().writeDocument(document, config.isOverrideBrokerSettings());
      permissionsToStore.add(new PermissionInfo(dataSourceId, SpDataStream.class));
    }
  }

  @Override
  protected void handlePipeline(String document, String pipelineId) throws JsonProcessingException {
    if (shouldStore(pipelineId, config.getPipelines())) {
      new PipelineResolver().writeDocument(document, config.isOverrideBrokerSettings());
      permissionsToStore.add(new PermissionInfo(pipelineId, Pipeline.class));
    }
  }

  @Override
  protected void handleDataLakeMeasure(String document, String dataLakeMeasureId) throws JsonProcessingException {
    if (shouldStore(dataLakeMeasureId, config.getDataLakeMeasures())) {
      new MeasurementResolver().writeDocument(document);
      permissionsToStore.add(new PermissionInfo(dataLakeMeasureId, DataLakeMeasure.class));
    }
  }

  @Override
  protected void handleDashboardWidget(String document, String dashboardWidgetId) throws JsonProcessingException {
    new DashboardWidgetResolver().writeDocument(document);
  }

  @Override
  protected void handleDataViewWidget(String document, String dataViewWidget) throws JsonProcessingException {
    new DataViewWidgetResolver().writeDocument(document);
  }

  @Override
  protected void handleFile(String document,
                            String fileMetadataId,
                            Map<String, byte[]> zipContent) throws IOException {
    var resolver = new FileResolver();
    var fileMetadata = resolver.readDocument(document);
    resolver.writeDocument(document);
    byte[] file = zipContent.get(
        fileMetadata.getInternalFilename().substring(0, fileMetadata.getInternalFilename().lastIndexOf(".")));
    new FileHandler().storeFile(fileMetadata.getInternalFilename(), new ByteArrayInputStream(file));
  }

  @Override
  protected Void getReturnObject() {
    return null;
  }

  @Override
  protected void afterResourcesCreated() {
    var resourceManager = new PermissionResourceManager();
    this.permissionsToStore
        .forEach(info -> resourceManager.createDefault(
            info.getInstanceId(),
            info.getInstanceClass(),
            this.ownerSid,
            true));
  }

  private boolean shouldStore(String adapterId,
                              Set<ExportItem> adapters) {
    return adapters
        .stream()
        .filter(item -> item.getResourceId().equals(adapterId))
        .allMatch(ExportItem::isSelected);
  }

}
