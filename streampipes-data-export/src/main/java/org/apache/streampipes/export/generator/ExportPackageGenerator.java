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

import org.apache.streampipes.commons.exceptions.ElementNotFoundException;
import org.apache.streampipes.export.resolver.AbstractResolver;
import org.apache.streampipes.export.resolver.AdapterResolver;
import org.apache.streampipes.export.resolver.DashboardResolver;
import org.apache.streampipes.export.resolver.DashboardWidgetResolver;
import org.apache.streampipes.export.resolver.DataSourceResolver;
import org.apache.streampipes.export.resolver.DataViewResolver;
import org.apache.streampipes.export.resolver.DataViewWidgetResolver;
import org.apache.streampipes.export.resolver.FileResolver;
import org.apache.streampipes.export.resolver.MeasurementResolver;
import org.apache.streampipes.export.resolver.PipelineResolver;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.model.export.StreamPipesApplicationPackage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExportPackageGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(ExportPackageGenerator.class);

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
        .collect(Collectors.toList()), manifest);

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

      config.getFiles().forEach(item -> {
        var fileResolver = new FileResolver();
        String filename = fileResolver.findDocument(item.getResourceId()).getFilename();
        addDoc(builder, item, new FileResolver(), manifest::addFile);
        try {
          builder.addBinary(filename, Files.readAllBytes(FileManager.getFile(filename).toPath()));
        } catch (IOException e) {
          e.printStackTrace();
        }
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
    } catch (JsonProcessingException | ElementNotFoundException e) {
      LOG.warn(
          "Could not find document with resource id {} with resolver {}",
          exportItem.getResourceId(),
          resolver.getClass().getCanonicalName(),
          e);
    }
  }

  private String sanitize(String resourceId) {
    return resourceId.replaceAll(":", "").replaceAll("\\.", "");
  }

  private void addAssets(ZipFileBuilder builder,
                         List<String> assetIds,
                         StreamPipesApplicationPackage manifest) {
    assetIds.forEach(assetId -> {
      try {
        var asset = getAsset(assetId);
        asset.remove("_rev");
        builder.addText(String.valueOf(asset.get("_id")), this.defaultMapper.writeValueAsString(asset));
        manifest.addAsset(String.valueOf(asset.get("_id")));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private Map<String, Object> getAsset(String assetId) throws IOException {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage().findOne(assetId);
  }
}
