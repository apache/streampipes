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

package org.apache.streampipes.service.core.migrations.v970;

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.model.assets.AssetLinkType;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ModifyAssetLinkTypeMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(ModifyAssetLinkTypeMigration.class);

  private final IGenericStorage genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ModifyAssetLinkTypeMigration() {

  }

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    getAssetLinkTypes().stream()
        .filter(assetLinkType -> switch (assetLinkType.getLinkType()) {
          case "data-view", "adapter", "pipeline" -> true;
          default -> false;
        })
        .forEach(assetLinkType -> {
          assetLinkType.setNavPaths(switch (assetLinkType.getLinkType()) {
            case "data-view" -> List.of("dataexplorer", "dashboard");
            case "adapter" -> List.of("connect", "details");
            case "pipeline" -> List.of("pipelines", "details");
            default -> throw new IllegalStateException("Unexpected value: " + assetLinkType.getLinkType());
          });
          updateLinkType(assetLinkType);
        });
  }

  @Override
  public String getDescription() {
    return "Modifying navigation targets of asset link types";
  }

  private void updateLinkType(AssetLinkType assetLinkType) {
    try {
      this.genericStorage.update(assetLinkType.getId(), objectMapper.writeValueAsString(assetLinkType));
    } catch (IOException e) {
      LOG.warn("Could not update asset link type {}", assetLinkType, e);
    }
  }

  private List<AssetLinkType> getAssetLinkTypes() {
    try {
      return deserialize(genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE));
    } catch (IOException e) {
      return List.of();
    }
  }

  private List<AssetLinkType> deserialize(List<Map<String, Object>> list) {
    CollectionType listType = objectMapper.getTypeFactory()
        .constructCollectionType(List.class, AssetLinkType.class);

    return objectMapper.convertValue(list, listType);
  }
}
