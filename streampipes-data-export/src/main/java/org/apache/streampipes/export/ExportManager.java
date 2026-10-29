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

import org.apache.streampipes.export.generator.ExportPackageGenerator;
import org.apache.streampipes.export.resolver.GenericStorageResolverProvider;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExportManager {

  public static ExportConfiguration getExportPreview(List<String> selectedAssetIds,
                                                     ExtensionServiceRequestManager extensionServiceRequestManager,
                                                     SpResourceManager resourceManager,
                                                     PipelineManager pipelineManager) {
    var exportConfig = new ExportConfiguration();
    var assetExportConfigurations = selectedAssetIds
        .stream()
        .map(assetId -> new AssetLinkResolver(
            assetId, extensionServiceRequestManager, resourceManager, pipelineManager)
            .resolveResources())
        .collect(Collectors.toList());
    exportConfig.setAssetExportConfiguration(assetExportConfigurations);
    addGenericStoragePreview(exportConfig, resourceManager.getGenericStorage());

    return exportConfig;
  }

  public static byte[] getExportPackage(ExportConfiguration exportConfiguration,
                                        ExtensionServiceRequestManager extensionServiceRequestManager,
                                        SpResourceManager resourceManager,
                                        PipelineManager pipelineManager) throws IOException {
    return new ExportPackageGenerator(
        exportConfiguration, extensionServiceRequestManager, resourceManager, pipelineManager)
        .generateExportPackage();
  }

  private static void addGenericStoragePreview(ExportConfiguration config, IGenericStorage storage) {
    var resolvers = new GenericStorageResolverProvider(storage);
    try {
      for (var appDocType : storage.getAllAppDocTypes()) {
        if (SpAssetModel.APP_DOC_TYPE.equals(appDocType)) {
          continue;
        }
        var exportType = GenericStorageResourceRegistry.INSTANCE.find(appDocType).filter(type -> type.category() != null);
        if (exportType.isPresent()) {
          // Fall back to the existing bulk export option if document enumeration fails.
          try {
            var items = new ArrayList<ExportItem>();
            for (var document : storage.findAll(appDocType)) {
              items.add(resolvers.toExportItem(document, false));
            }
            config.getGenericStorageDocuments().addAll(items);
          } catch (IOException e) {
            config.addGenericStorageAppDocType(new ExportItem(appDocType, appDocType, false));
          }
        } else {
          config.addGenericStorageAppDocType(new ExportItem(appDocType, appDocType, false));
        }
      }
    } catch (IOException e) {
      // Preserve the asset preview if generic storage is unavailable.
    }
  }

}
