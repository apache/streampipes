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

package org.apache.streampipes.export;

import org.apache.streampipes.export.constants.ResolvableAssetLinks;
import org.apache.streampipes.export.resolver.AdapterResolver;
import org.apache.streampipes.export.resolver.ChartResolver;
import org.apache.streampipes.export.resolver.DashboardResolver;
import org.apache.streampipes.export.resolver.DataSourceResolver;
import org.apache.streampipes.export.resolver.FileResolver;
import org.apache.streampipes.export.resolver.MeasurementResolver;
import org.apache.streampipes.export.resolver.PipelineResolver;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.assets.AssetLink;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssetLinkResolver {

  private static final Logger LOG = LoggerFactory.getLogger(AssetLinkResolver.class);

  private final String assetId;
  private final ObjectMapper mapper;
  private final ExtensionServiceRequestManager extensionServiceRequestManager;
  private final PipelineManager pipelineManager;
  private final SpResourceManager resourceManager;

  public AssetLinkResolver(String assetId,
                           ExtensionServiceRequestManager extensionServiceRequestManager,
                           SpResourceManager resourceManager,
                           PipelineManager pipelineManager) {
    this.assetId = assetId;
    this.extensionServiceRequestManager = extensionServiceRequestManager;
    this.mapper = JacksonSerializer.getObjectMapper(Map.of(
      DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true,
      SerializationFeature.INDENT_OUTPUT, false 
    ));
    this.pipelineManager = pipelineManager;
    this.resourceManager = resourceManager;
  }

  public AssetExportConfiguration resolveResources() {

    try {
      var asset = getAsset();
      var assetLinks = new AssetLinkCollector(asset).collectAssetLinks();
      var exportConfig = new AssetExportConfiguration();
      exportConfig.setAssetId(this.assetId);
      exportConfig.setAssetName(asset.getAssetName());
      exportConfig.setAdapters(new AdapterResolver(
          extensionServiceRequestManager,
          resourceManager
      )
          .resolve(getLinks(assetLinks, ResolvableAssetLinks.ADAPTER)));
      exportConfig.setDataViews(new ChartResolver(resourceManager)
          .resolve(getLinks(assetLinks, ResolvableAssetLinks.CHART)));
      exportConfig.setDashboards(new DashboardResolver(
          resourceManager.manageDashboards()
      ).resolve(getLinks(assetLinks, ResolvableAssetLinks.DASHBOARD)));
      exportConfig.setDataSources(
          new DataSourceResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.DATA_SOURCE)));
      exportConfig.setPipelines(
          new PipelineResolver(extensionServiceRequestManager, pipelineManager, resourceManager.managePipelines())
          .resolve(getLinks(assetLinks, ResolvableAssetLinks.PIPELINE))
      );
      exportConfig.setDataLakeMeasures(
          new MeasurementResolver(
              resourceManager.manageDataLakeMeasures().getDb()
          ).resolve(getLinks(assetLinks, ResolvableAssetLinks.MEASUREMENT)));
      exportConfig.setFiles(new FileResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.FILE)));

      return exportConfig;
    } catch (IOException e) {
      LOG.error("IO Exception when writing export configuration", e);
      return new AssetExportConfiguration();
    }
  }

  private Set<AssetLink> getLinks(Set<AssetLink> assetLinks,
                                  String queryHint) {
    return assetLinks
        .stream()
        .filter(link -> link.getQueryHint().equals(queryHint))
        .collect(Collectors.toSet());
  }

  private SpAssetModel getAsset() throws IOException {
    return deserialize(StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage().findOne(this.assetId));
  }

  private SpAssetModel deserialize(Map<String, Object> asset) {
    return this.mapper.convertValue(asset, SpAssetModel.class);
  }

}
