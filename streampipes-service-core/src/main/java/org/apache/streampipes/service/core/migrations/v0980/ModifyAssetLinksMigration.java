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

package org.apache.streampipes.service.core.migrations.v0980;

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModifyAssetLinksMigration implements Migration {

  private final IGenericStorage storage = StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();

  @Override
  public boolean shouldExecute() {
    try {
      return storage
          .findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE)
          .stream().anyMatch(al -> al.get("linkType").equals("data-view"));
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    var assets = storage.findAll(GenericDocTypes.DOC_ASSET_MANGEMENT);
    for (Map<String, Object> asset : assets) {
      updateAssetLink(asset);
      storage.update(asset.get("_id").toString(), new ObjectMapper().writeValueAsString(asset));
    }
  }

  @SuppressWarnings("unchecked")
  private void updateAssetLink(Map<String, Object> asset) {
    if (asset.containsKey("assetLinks")) {
      List<Map<String, Object>> assetLinks = castToListOfMaps(asset.get("assetLinks"));

      assetLinks.forEach(assetLink -> {
        Optional.ofNullable(assetLink.get("linkType"))
            .filter(linkType -> linkType.equals("data-view"))
            .ifPresent(linkType -> {
              assetLink.put("linkType", "chart");
              assetLink.put("queryHint", "chart");
            });
      });
    }

    if (asset.containsKey("assets")) {
      List<Map<String, Object>> nestedAssets = castToListOfMaps(asset.get("assets"));

      nestedAssets.forEach(this::updateAssetLink);
    }
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> castToListOfMaps(Object obj) {
    if (obj instanceof List<?>) {
      return ((List<?>) obj).stream()
          .filter(item -> item instanceof Map<?, ?>)
          .map(item -> (Map<String, Object>) item)
          .toList();
    } else {
      throw new IllegalArgumentException("Expected a List of Maps but got: " + obj);
    }
  }

  @Override
  public String getDescription() {
    return "Migrating asset links of type 'data-view' to type 'chart'";
  }
}
