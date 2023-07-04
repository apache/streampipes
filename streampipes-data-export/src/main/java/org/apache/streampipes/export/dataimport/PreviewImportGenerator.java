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


import org.apache.streampipes.export.resolver.AdapterResolver;
import org.apache.streampipes.export.resolver.DashboardResolver;
import org.apache.streampipes.export.resolver.DataSourceResolver;
import org.apache.streampipes.export.resolver.DataViewResolver;
import org.apache.streampipes.export.resolver.FileResolver;
import org.apache.streampipes.export.resolver.MeasurementResolver;
import org.apache.streampipes.export.resolver.PipelineResolver;
import org.apache.streampipes.export.utils.ImportAdapterMigrationUtils;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

public class PreviewImportGenerator extends ImportGenerator<AssetExportConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(PreviewImportGenerator.class);
  private final AssetExportConfiguration importConfig;

  public PreviewImportGenerator() {
    super();
    this.importConfig = new AssetExportConfiguration();

  }

  private void addExportItem(String id,
                             String name,
                             Consumer<ExportItem> addAdapter) {
    var item = new ExportItem(id, name, true);
    addAdapter.accept(item);
  }


  @Override
  protected void handleAsset(Map<String, byte[]> previewFiles, String assetId) throws JsonProcessingException {
    Map<String, Object> assetDescription = this.defaultMapper.readValue(asString(previewFiles.get(assetId)),
        new TypeReference<>() {
        });
    importConfig.addAsset(new ExportItem(assetId, String.valueOf(assetDescription.get("assetName")), true));
  }

  @Override
  protected void handleAdapter(String document,
                               String adapterId) throws JsonProcessingException {
    try {
      var convertedDoc = ImportAdapterMigrationUtils.checkAndPerformMigration(document);
      addExportItem(adapterId, new AdapterResolver().readDocument(convertedDoc).getName(), importConfig::addAdapter);
    } catch (IllegalArgumentException e) {
      LOG.warn("Skipping import of data set adapter {}", adapterId);
    }
  }

  @Override
  protected void handleDashboard(String document, String dashboardId) throws JsonProcessingException {
    addExportItem(dashboardId, new DashboardResolver().readDocument(document).getName(), importConfig::addDashboard);
  }

  @Override
  protected void handleDataView(String document, String dataViewId) throws JsonProcessingException {
    addExportItem(dataViewId, new DataViewResolver().readDocument(document).getName(), importConfig::addDataView);
  }

  @Override
  protected void handleDataSource(String document, String dataSourceId) throws JsonProcessingException {
    addExportItem(dataSourceId, new DataSourceResolver().readDocument(document).getName(), importConfig::addDataSource);
  }

  @Override
  protected void handlePipeline(String document, String pipelineId) throws JsonProcessingException {
    addExportItem(pipelineId, new PipelineResolver().readDocument(document).getName(), importConfig::addPipeline);
  }

  @Override
  protected void handleDataLakeMeasure(String document, String measurementId) throws JsonProcessingException {
    addExportItem(measurementId, new MeasurementResolver().readDocument(document).getMeasureName(),
        importConfig::addDataLakeMeasure);
  }

  @Override
  protected void handleDashboardWidget(String document, String dashboardWidgetId) {

  }

  @Override
  protected void handleDataViewWidget(String document, String dataViewWidget) {

  }

  @Override
  protected void handleFile(String document,
                            String fileMetadataId,
                            Map<String, byte[]> zipContent) throws JsonProcessingException {
    addExportItem(fileMetadataId, new FileResolver().readDocument(document).getOriginalFilename(),
        importConfig::addFile);
  }

  @Override
  protected AssetExportConfiguration getReturnObject() {
    return this.importConfig;
  }

  @Override
  protected void afterResourcesCreated() {
  }
}
