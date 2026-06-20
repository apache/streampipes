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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ExportManager {

  public static ExportConfiguration getExportPreview(List<String> selectedAssetIds,
                                                     ExtensionServiceRequestManager extensionServiceRequestManager) {
    var exportConfig = new ExportConfiguration();
    var assetExportConfigurations = selectedAssetIds
        .stream()
        .map(assetId -> new AssetLinkResolver(assetId, extensionServiceRequestManager).resolveResources())
        .collect(Collectors.toList());
    var genericStorageAppDocTypes = getGenericStorageAppDocTypes();

    exportConfig.setAssetExportConfiguration(assetExportConfigurations);
    exportConfig.setGenericStorageAppDocTypes(genericStorageAppDocTypes);

    return exportConfig;
  }

  public static byte[] getExportPackage(ExportConfiguration exportConfiguration,
                                        ExtensionServiceRequestManager extensionServiceRequestManager)
      throws IOException {
    return new ExportPackageGenerator(exportConfiguration, extensionServiceRequestManager).generateExportPackage();
  }

  private static List<ExportItem> getGenericStorageAppDocTypes() {
    try {
      return StorageDispatcher.INSTANCE.getNoSqlStore()
          .getGenericStorage()
          .getAllAppDocTypes()
          .stream()
          .filter(appDocType -> !SpAssetModel.APP_DOC_TYPE.equals(appDocType))
          .map(appDocType -> new ExportItem(appDocType, appDocType, false))
          .collect(Collectors.toList());
    } catch (IOException e) {
        return List.of();
    }
  }

}
