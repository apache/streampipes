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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.commons.zip.ZipFileExtractor;
import org.apache.streampipes.export.constants.ExportConstants;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.export.StreamPipesApplicationPackage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class ImportGenerator<T> {

  protected ObjectMapper spMapper;
  protected ObjectMapper defaultMapper;

  public ImportGenerator() {
    this.spMapper = SerializationUtils.getSpObjectMapper();
    this.defaultMapper = SerializationUtils.getDefaultObjectMapper();
  }

  public T generate(InputStream inputStream) throws IOException {
    Map<String, byte[]> previewFiles = new ZipFileExtractor(inputStream).extractZipToMap();

    var manifest = getManifest(previewFiles);

    for (String assetId: manifest.getAssets()) {
      handleAsset(previewFiles, assetId);
    }

    for (String adapterId: manifest.getAdapters()) {
      handleAdapter(asString(previewFiles.get(adapterId)), adapterId);
    }

    for(String dashboardId: manifest.getDashboards()) {
      handleDashboard(asString(previewFiles.get(dashboardId)), dashboardId);
    }

    for (String dataViewId: manifest.getDataViews()) {
      handleDataView(asString(previewFiles.get(dataViewId)), dataViewId);
    }

    for (String dataSourceId: manifest.getDataSources()) {
      handleDataSource(asString(previewFiles.get(dataSourceId)), dataSourceId);
    }

    for (String pipelineId: manifest.getPipelines()) {
      handlePipeline(asString(previewFiles.get(pipelineId)), pipelineId);
    }

    for (String measurementId: manifest.getDataLakeMeasures()) {
      handleDataLakeMeasure(asString(previewFiles.get(measurementId)), measurementId);
    }

    for (String dashboardWidgetId: manifest.getDashboardWidgets()) {
      handleDashboardWidget(asString(previewFiles.get(dashboardWidgetId)), dashboardWidgetId);
    }

    for (String dataViewWidgetId: manifest.getDataViewWidgets()) {
      handleDataViewWidget(asString(previewFiles.get(dataViewWidgetId)), dataViewWidgetId);
    }

    for(String fileMetadataId: manifest.getFiles()) {
      handleFile(asString(previewFiles.get(fileMetadataId)), fileMetadataId, previewFiles);
    }

    afterResourcesCreated();

    return getReturnObject();
  }

  protected String asString(byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private StreamPipesApplicationPackage getManifest(Map<String, byte[]> previewFiles) throws JsonProcessingException {
    return this.defaultMapper.readValue(asString(previewFiles.get(ExportConstants.MANIFEST)), StreamPipesApplicationPackage.class);
  }

  protected abstract void handleAsset(Map<String, byte[]> previewFiles, String assetId) throws IOException;
  protected abstract void handleAdapter(String document, String adapterId) throws JsonProcessingException;
  protected abstract void handleDashboard(String document, String dashboardId) throws JsonProcessingException;
  protected abstract void handleDataView(String document, String dataViewId) throws JsonProcessingException;
  protected abstract void handleDataSource(String document, String dataSourceId) throws JsonProcessingException;
  protected abstract void handlePipeline(String document, String pipelineId) throws JsonProcessingException;
  protected abstract void handleDataLakeMeasure(String document, String dataLakeMeasureId) throws JsonProcessingException;
  protected abstract void handleDashboardWidget(String document, String dashboardWidgetId) throws JsonProcessingException;
  protected abstract void handleDataViewWidget(String document, String dataViewWidgetId) throws JsonProcessingException;
  protected abstract void handleFile(String document, String fileMetadataId, Map<String, byte[]> zipContent) throws IOException;

  protected abstract T getReturnObject();

  protected abstract void afterResourcesCreated();

}
