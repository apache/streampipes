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

package org.apache.streampipes.export.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.export.resolver.*;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.model.export.StreamPipesApplicationPackage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExportPackageGenerator {

  private final ExportConfiguration exportConfiguration;
  private ObjectMapper defaultMapper;
  private ObjectMapper spMapper;

  public ExportPackageGenerator(ExportConfiguration exportConfiguration) {
    this.exportConfiguration = exportConfiguration;
    this.defaultMapper = SerializationUtils.getDefaultObjectMapper();
    this.spMapper = SerializationUtils.getSpObjectMapper();
  }

  public byte[] generateExportPackage() throws IOException {
    ZipFileBuilder builder = ZipFileBuilder.create();
    var manifest = new StreamPipesApplicationPackage();

    addAssets(builder, exportConfiguration
      .getAssetExportConfiguration()
      .stream()
      .map(AssetExportConfiguration::getAssetId)
      .collect(Collectors.toList()));

    this.exportConfiguration.getAssetExportConfiguration().forEach(config -> {

      config.getAdapters().forEach(item -> addDoc(builder,
        item,
        new AdapterResolver(),
        manifest::addAdapter));

      config.getDashboards().forEach(item -> {
        var resolver = new DashboardResolver();
        addDoc(builder,
          item,
          resolver,
          manifest::addDashboard);

        var widgets = resolver.getWidgets(item.getResourceId());
        var widgetResolver = new DashboardWidgetResolver();
        widgets.forEach(widgetId -> addDoc(builder, widgetId, widgetResolver, manifest::addDashboardWidget));
      });

      config.getDataSources().forEach(item -> addDoc(builder,
        item,
        new DataSourceResolver(),
        manifest::addDataSource));

      config.getDataLakeMeasures().forEach(item -> addDoc(builder,
        item,
        new MeasurementResolver(),
        manifest::addDataLakeMeasure));

      config.getPipelines().forEach(item -> addDoc(builder,
        item,
        new PipelineResolver(),
        manifest::addPipeline));

      config.getDataViews().forEach(item -> {
        var resolver = new DataViewResolver();
        addDoc(builder,
          item,
          resolver,
          manifest::addDataView);

        var widgets = resolver.getWidgets(item.getResourceId());
        var widgetResolver = new DataViewWidgetResolver();
        widgets.forEach(widgetId -> addDoc(builder, widgetId, widgetResolver, manifest::addDataViewWidget));
      });
    });

    builder.addManifest(defaultMapper.writeValueAsString(manifest));


    return builder.buildZip();
  }

  private void addDoc(ZipFileBuilder builder,
                      String resourceId,
                      AbstractResolver<?> resolver,
                      Consumer<String> function) {
    addDoc(builder, new ExportItem(resourceId, "", true), resolver, function);
  }

  private void addDoc(ZipFileBuilder builder,
                      ExportItem exportItem,
                      AbstractResolver<?> resolver,
                      Consumer<String> function) {
    try {
      var resourceId = exportItem.getResourceId();
      var sanitizedResourceId = sanitize(resourceId);
      builder.addText(sanitizedResourceId, resolver.getSerializedDocument(resourceId));
      function.accept(sanitizedResourceId);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private String sanitize(String resourceId) {
    return resourceId.replaceAll(":", "").replaceAll("\\.", "");
  }

  private void addAssets(ZipFileBuilder builder,
                         List<String> assetIds) {
    assetIds.forEach(assetId -> {
      try {
        var asset = getAsset(assetId);
        builder.addText(String.valueOf(asset.get("_id")), this.defaultMapper.writeValueAsString(asset));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private Map<String, Object> getAsset(String assetId) throws IOException {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage().findOne(assetId);
  }
}
