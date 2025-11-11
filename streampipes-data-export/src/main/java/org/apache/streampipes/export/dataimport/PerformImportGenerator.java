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
import org.apache.streampipes.export.resolver.AbstractResolver;
import org.apache.streampipes.export.resolver.AdapterResolver;
import org.apache.streampipes.export.resolver.ChartResolver;
import org.apache.streampipes.export.resolver.DashboardResolver;
import org.apache.streampipes.export.resolver.DataSourceResolver;
import org.apache.streampipes.export.resolver.FileResolver;
import org.apache.streampipes.export.resolver.GenericStorageDocumentResolver;
import org.apache.streampipes.export.resolver.MeasurementResolver;
import org.apache.streampipes.export.resolver.PipelineResolver;
import org.apache.streampipes.manager.file.FileHandler;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
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

  private final AssetExportConfiguration config;
  private final INoSqlStorage storage;
  private final Set<PermissionInfo> permissionsToStore = new HashSet<>();
  private final String ownerSid;

  public PerformImportGenerator(AssetExportConfiguration config,
                                String ownerSid) {
    this.config = config;
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore();
    this.ownerSid = ownerSid;
  }

  @Override
  protected void handleAsset(Map<String, byte[]> previewFiles, String assetId) throws IOException {
    var document = asString(previewFiles.get(assetId));
    try {
      var existing = storage.getGenericStorage().findOne(assetId);
      if (config.isOverwriteExistingDocuments()) {
        storage.getGenericStorage().delete(assetId, existing.get("_rev").toString());
      }
    } catch (IOException e) {
      // Document not found, do nothing
    }
    storage.getGenericStorage().create(document);
  }

  @Override
  protected void handleAdapter(String document, String adapterId) throws JsonProcessingException {
    if (shouldStore(adapterId, config.getAdapters())) {
      writeDocument(document, new AdapterResolver());
      // adapters do not have permissions associated
    }
  }

  @Override
  protected void handleChart(String document, String chartId) throws JsonProcessingException {
    if (shouldStore(chartId, config.getDataViews())) {
      writeDocument(document, new ChartResolver());
      var chart = new ChartResolver().deserializeDocument(document);
      permissionsToStore.add(new PermissionInfo(chart.getElementId(), DataExplorerWidgetModel.class));
    }
  }

  @Override
  protected void handleDashboard(String document, String dashboardId) throws JsonProcessingException {
    if (shouldStore(dashboardId, config.getDashboards())) {
      writeDocument(document, new DashboardResolver());
      var dashboard = new DashboardResolver().deserializeDocument(document);
      permissionsToStore.add(new PermissionInfo(dashboard.getElementId(), DashboardModel.class));
    }
  }

  @Override
  protected void handleDataSource(String document, String dataSourceId) throws JsonProcessingException {
    if (shouldStore(dataSourceId, config.getDataSources())) {
      writeDocument(document, new DataSourceResolver());
      var dataStream = new DataSourceResolver().deserializeDocument(document);
      permissionsToStore.add(new PermissionInfo(dataStream.getElementId(), SpDataStream.class));
    }
  }

  @Override
  protected void handlePipeline(String document, String pipelineId) throws JsonProcessingException {
    if (shouldStore(pipelineId, config.getPipelines())) {
      writeDocument(document, new PipelineResolver());
      permissionsToStore.add(new PermissionInfo(pipelineId, Pipeline.class));
    }
  }

  @Override
  protected void handleDataLakeMeasure(String document, String dataLakeMeasureId) throws JsonProcessingException {
    if (shouldStore(dataLakeMeasureId, config.getDataLakeMeasures())) {
      writeDocument(document, new MeasurementResolver());
      permissionsToStore.add(new PermissionInfo(dataLakeMeasureId, DataLakeMeasure.class));
    }
  }

  @Override
  protected void handleFile(String document,
                            String fileMetadataId,
                            Map<String, byte[]> zipContent) throws IOException {
    var resolver = new FileResolver();
    var fileMetadata = resolver.readDocument(document);
    writeDocument(document, resolver);
    byte[] file = zipContent.get(
        fileMetadata.getFilename().substring(0, fileMetadata.getFilename().lastIndexOf(".")));
    new FileHandler().storeFile(fileMetadata.getFilename(), new ByteArrayInputStream(file));
  }

  @Override
  protected void handleGenericStorageDocument(String document, String documentId) throws JsonProcessingException {
    if (shouldStore(documentId, config.getGenericStorageDocuments())) {
      writeDocument(document, new GenericStorageDocumentResolver());
    }
  }

  private void writeDocument(String document,
                             AbstractResolver<?> resolver) throws JsonProcessingException {
    if (config.isOverwriteExistingDocuments()) {
      resolver.deleteDocument(document);
    }
    resolver.writeDocument(document, config);
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

  private boolean shouldStore(String documentId,
                              Set<ExportItem> exportItems) {
    return exportItems
        .stream()
        .filter(item -> item.getResourceId().equals(documentId))
        .allMatch(ExportItem::isSelected);
  }
}
