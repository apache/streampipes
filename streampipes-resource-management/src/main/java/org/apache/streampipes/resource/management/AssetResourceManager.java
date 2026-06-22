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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.assets.AssetSummaryDto;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.system.IAssetStorage;

import org.springframework.security.core.Authentication;

public class AssetResourceManager extends CrudResourceManager<SpAssetModel, IAssetStorage> {

  private final SpPermissionEvaluator permissionEvaluator;

  public AssetResourceManager(IAssetStorage assetStorage,
                              PermissionResourceManager permissionResourceManager) {
    super(assetStorage, SpAssetModel.class, permissionResourceManager);
    this.permissionEvaluator = new SpPermissionEvaluator(permissionResourceManager.getDb());
  }

  public ResourceSummaryDto<AssetSummaryDto> getSummary(Authentication auth) {
    var assets = db.findAll().stream()
        .filter(asset -> canReadAsset(auth, asset))
        .map(this::toSummary)
        .toList();

    return new ResourceSummaryDto<>(assets, assets.size());
  }

  private boolean canReadAsset(Authentication auth, SpAssetModel asset) {
    return asset != null
        && asset.getElementId() != null
        && permissionEvaluator.hasPermission(auth, asset.getElementId(), "READ");
  }

  private AssetSummaryDto toSummary(SpAssetModel asset) {
    return new AssetSummaryDto(
        asset.getElementId(),
        asset.getAssetName(),
        asset.getAssetDescription(),
        asset.isRemovable()
    );
  }
}
