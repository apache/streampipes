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
import org.apache.streampipes.export.resolver.DashboardResolver;
import org.apache.streampipes.export.resolver.DataSourceResolver;
import org.apache.streampipes.export.resolver.DataViewResolver;
import org.apache.streampipes.export.resolver.FileResolver;
import org.apache.streampipes.export.resolver.MeasurementResolver;
import org.apache.streampipes.export.resolver.PipelineResolver;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.assets.AssetLink;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssetLinkResolver {

  private final String assetId;
  private final ObjectMapper mapper;

  public AssetLinkResolver(String assetId) {
    this.assetId = assetId;
    this.mapper = SerializationUtils.getDefaultObjectMapper();
  }

  public AssetExportConfiguration resolveResources() {

    try {
      var asset = getAsset();
      var assetLinks = new AssetLinkCollector(asset).collectAssetLinks();
      var exportConfig = new AssetExportConfiguration();
      exportConfig.setAssetId(this.assetId);
      exportConfig.setAssetName(asset.getAssetName());
      exportConfig.setAdapters(new AdapterResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.ADAPTER)));
      exportConfig.setDashboards(new DashboardResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.DASHBOARD)));
      exportConfig.setDataViews(new DataViewResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.DATA_VIEW)));
      exportConfig.setDataSources(
          new DataSourceResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.DATA_SOURCE)));
      exportConfig.setPipelines(new PipelineResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.PIPELINE)));
      exportConfig.setDataLakeMeasures(
          new MeasurementResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.MEASUREMENT)));
      exportConfig.setFiles(new FileResolver().resolve(getLinks(assetLinks, ResolvableAssetLinks.FILE)));

      return exportConfig;
    } catch (IOException e) {
      e.printStackTrace();
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
