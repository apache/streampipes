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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MoveAssetContentMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(MoveAssetContentMigration.class);

  private IGenericStorage genericStorage;
  private final ObjectMapper mapper = new ObjectMapper();

  private static final Set<String> allowedKeys = Set.of(
      "assetId",
      "assetName",
      "assetDescription",
      "assetType",
      "assetLinks",
      "assetSite",
      "assets",
      "_id",
      "_rev",
      "removable",
      "additionalData",
      "labelIds",
      "appDocType"
  );

  public MoveAssetContentMigration() {
    this.genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();
  }

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    var allAssets = genericStorage.findAll(SpAssetModel.APP_DOC_TYPE);
    allAssets.forEach(a -> {
      try {
        migrate(a);
        genericStorage.update(a.get("_id").toString(), mapper.writeValueAsString(a));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

  }

  @Override
  public String getDescription() {
    return "Move optional asset properties to additionalProperties";
  }

  private void migrate(Map<String, Object> asset) {
    var additionalProperties = (Map<String, Object>) asset.computeIfAbsent(
        "additionalData",
        key -> new HashMap<String, Object>()
    );

    // Move non-allowed keys (except "assets" and "additionalProperties" itself)
    var iterator = asset.entrySet().iterator();
    while (iterator.hasNext()) {
      var entry = iterator.next();
      var key = entry.getKey();

      if ("assets".equals(key) || "additionalData".equals(key)) {
        continue;
      }

      if (!allowedKeys.contains(key)) {
        additionalProperties.put(key, entry.getValue());
        iterator.remove();
      }
    }

    var assetsValue = asset.get("assets");
    if (assetsValue instanceof List<?> assets && !assets.isEmpty()) {
      for (var element : assets) {
        if (element instanceof Map<?, ?> childMap) {
          migrate((Map<String, Object>) childMap);
        } else {
          LOG.warn("Unexpected element type in 'assets': {}", element.getClass());
        }
      }
    }
  }
}
