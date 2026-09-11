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

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RenameAssetLinkTypesMigration implements Migration {

  private static final String FIELD_ID = "_id";
  private static final String FIELD_LINK_LABEL = "linkLabel";
  private static final String FIELD_LINK_TYPE = "linkType";
  private static final String FIELD_QUERY_HINT = "queryHint";
  private static final String FIELD_LINK_QUERY_HINT = "linkQueryHint";
  private static final String FIELD_ASSET_LINKS = "assetLinks";
  private static final String FIELD_ASSETS = "assets";

  private static final Map<String, RenamedLinkType> RENAMED_LINK_TYPES = Map.of(
      "data-source", new RenamedLinkType("data-stream", "Data Stream"),
      "measurement", new RenamedLinkType("dataset", "Dataset")
  );

  private final IGenericStorage genericStorage;
  private final ObjectMapper mapper;

  public RenameAssetLinkTypesMigration() {
    this(StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage(), new ObjectMapper());
  }

  RenameAssetLinkTypesMigration(IGenericStorage genericStorage, ObjectMapper mapper) {
    this.genericStorage = genericStorage;
    this.mapper = mapper;
  }

  @Override
  public boolean shouldExecute() {
    try {
      return genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE)
          .stream()
          .anyMatch(this::isLegacyLinkType)
          || genericStorage.findAll(GenericDocTypes.DOC_ASSET_MANAGEMENT)
          .stream()
          .anyMatch(this::containsLegacyAssetLink);
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    migrateAssetLinkTypes();
    migrateAssetLinks();
  }

  private void migrateAssetLinkTypes() throws IOException {
    List<Map<String, Object>> assetLinkTypes = genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE);
    for (Map<String, Object> assetLinkType : assetLinkTypes) {
      RenamedLinkType renamedLinkType = renamedLinkType(assetLinkType);
      if (renamedLinkType == null) {
        continue;
      }

      assetLinkType.put(FIELD_LINK_TYPE, renamedLinkType.type());
      assetLinkType.put(FIELD_LINK_LABEL, renamedLinkType.label());
      assetLinkType.put(FIELD_LINK_QUERY_HINT, renamedLinkType.type());
      update(assetLinkType);
    }
  }

  private void migrateAssetLinks() throws IOException {
    List<Map<String, Object>> assets = genericStorage.findAll(GenericDocTypes.DOC_ASSET_MANAGEMENT);
    for (Map<String, Object> asset : assets) {
      if (migrateAssetLinks(asset)) {
        update(asset);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private boolean migrateAssetLinks(Map<String, Object> asset) {
    boolean changed = false;
    Object assetLinks = asset.get(FIELD_ASSET_LINKS);
    if (assetLinks instanceof List<?> links) {
      for (Object link : links) {
        if (link instanceof Map<?, ?>) {
          Map<String, Object> assetLink = (Map<String, Object>) link;
          RenamedLinkType renamedLinkType = renamedLinkType(assetLink);
          if (renamedLinkType != null) {
            assetLink.put(FIELD_LINK_TYPE, renamedLinkType.type());
            assetLink.put(FIELD_QUERY_HINT, renamedLinkType.type());
            changed = true;
          }
        }
      }
    }

    Object nestedAssets = asset.get(FIELD_ASSETS);
    if (nestedAssets instanceof List<?> assets) {
      for (Object nestedAsset : assets) {
        if (nestedAsset instanceof Map<?, ?>) {
          changed |= migrateAssetLinks((Map<String, Object>) nestedAsset);
        }
      }
    }
    return changed;
  }

  private boolean containsLegacyAssetLink(Map<String, Object> asset) {
    return containsLegacyAssetLink(asset.get(FIELD_ASSET_LINKS))
        || containsLegacyAssetLink(asset.get(FIELD_ASSETS));
  }

  @SuppressWarnings("unchecked")
  private boolean containsLegacyAssetLink(Object value) {
    if (!(value instanceof List<?> values)) {
      return false;
    }

    return values.stream().anyMatch(valueItem -> valueItem instanceof Map<?, ?> map
        && (isLegacyLinkType((Map<String, Object>) map)
        || containsLegacyAssetLink((Map<String, Object>) map)));
  }

  private boolean isLegacyLinkType(Map<String, Object> assetLinkType) {
    return renamedLinkType(assetLinkType) != null;
  }

  private RenamedLinkType renamedLinkType(Map<String, Object> assetLinkType) {
    return RENAMED_LINK_TYPES.get(assetLinkType.get(FIELD_LINK_TYPE));
  }

  private void update(Map<String, Object> document) throws IOException {
    genericStorage.update(String.valueOf(document.get(FIELD_ID)), mapper.writeValueAsString(document));
  }

  @Override
  public String getDescription() {
    return "Renaming asset link types for data streams and datasets";
  }

  private record RenamedLinkType(String type, String label) {
  }
}
