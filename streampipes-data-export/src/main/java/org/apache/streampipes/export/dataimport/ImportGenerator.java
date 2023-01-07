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

import org.apache.streampipes.commons.zip.ZipFileExtractor;
import org.apache.streampipes.export.constants.ExportConstants;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.export.StreamPipesApplicationPackage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lightcouch.DocumentConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class ImportGenerator<T> {

  private static final Logger LOG = LoggerFactory.getLogger(ImportGenerator.class);

  protected ObjectMapper spMapper;
  protected ObjectMapper defaultMapper;

  public ImportGenerator() {
    this.spMapper = SerializationUtils.getSpObjectMapper();
    this.defaultMapper = SerializationUtils.getDefaultObjectMapper();
  }

  public T generate(InputStream inputStream) throws IOException {
    Map<String, byte[]> previewFiles = new ZipFileExtractor(inputStream).extractZipToMap();

    var manifest = getManifest(previewFiles);

    for (String assetId : manifest.getAssets()) {
      try {
        handleAsset(previewFiles, assetId);
      } catch (DocumentConflictException | IOException e) {
        LOG.warn("Skipping import of asset model {} (already present with the same id)", assetId);
      }
    }

    for (String adapterId : manifest.getAdapters()) {
      try {
        handleAdapter(asString(previewFiles.get(adapterId)), adapterId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of adapter {} (already present with the same id)", adapterId);
      }
    }

    for (String dashboardId : manifest.getDashboards()) {
      try {
        handleDashboard(asString(previewFiles.get(dashboardId)), dashboardId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of dashboard {} (already present with the same id)", dashboardId);
      }
    }

    for (String dataViewId : manifest.getDataViews()) {
      try {
        handleDataView(asString(previewFiles.get(dataViewId)), dataViewId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of data view {} (already present with the same id)", dataViewId);
      }
    }

    for (String dataSourceId : manifest.getDataSources()) {
      try {
        handleDataSource(asString(previewFiles.get(dataSourceId)), dataSourceId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of data source {} (already present with the same id)", dataSourceId);
      }
    }

    for (String pipelineId : manifest.getPipelines()) {
      try {
        handlePipeline(asString(previewFiles.get(pipelineId)), pipelineId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of pipeline {} (already present with the same id)", pipelineId);
      }
    }

    for (String measurementId : manifest.getDataLakeMeasures()) {
      try {
        handleDataLakeMeasure(asString(previewFiles.get(measurementId)), measurementId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of data lake measure {} (already present with the same id)", measurementId);
      }
    }

    for (String dashboardWidgetId : manifest.getDashboardWidgets()) {
      try {
        handleDashboardWidget(asString(previewFiles.get(dashboardWidgetId)), dashboardWidgetId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of dashboard widget {} (already present with the same id)", dashboardWidgetId);
      }
    }

    for (String dataViewWidgetId : manifest.getDataViewWidgets()) {
      try {
        handleDataViewWidget(asString(previewFiles.get(dataViewWidgetId)), dataViewWidgetId);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of data view widget {} (already present with the same id)", dataViewWidgetId);
      }
    }

    for (String fileMetadataId : manifest.getFiles()) {
      try {
        handleFile(asString(previewFiles.get(fileMetadataId)), fileMetadataId, previewFiles);
      } catch (DocumentConflictException e) {
        LOG.warn("Skipping import of file {} (already present with the same id)", fileMetadataId);
      }
    }

    afterResourcesCreated();

    return getReturnObject();
  }

  protected String asString(byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private StreamPipesApplicationPackage getManifest(Map<String, byte[]> previewFiles) throws JsonProcessingException {
    return this.defaultMapper.readValue(asString(previewFiles.get(ExportConstants.MANIFEST)),
        StreamPipesApplicationPackage.class);
  }

  protected abstract void handleAsset(Map<String, byte[]> previewFiles, String assetId) throws IOException;

  protected abstract void handleAdapter(String document, String adapterId) throws JsonProcessingException;

  protected abstract void handleDashboard(String document, String dashboardId) throws JsonProcessingException;

  protected abstract void handleDataView(String document, String dataViewId) throws JsonProcessingException;

  protected abstract void handleDataSource(String document, String dataSourceId) throws JsonProcessingException;

  protected abstract void handlePipeline(String document, String pipelineId) throws JsonProcessingException;

  protected abstract void handleDataLakeMeasure(String document, String dataLakeMeasureId)
      throws JsonProcessingException;

  protected abstract void handleDashboardWidget(String document, String dashboardWidgetId)
      throws JsonProcessingException;

  protected abstract void handleDataViewWidget(String document, String dataViewWidgetId) throws JsonProcessingException;

  protected abstract void handleFile(String document, String fileMetadataId, Map<String, byte[]> zipContent)
      throws IOException;

  protected abstract T getReturnObject();

  protected abstract void afterResourcesCreated();

}
