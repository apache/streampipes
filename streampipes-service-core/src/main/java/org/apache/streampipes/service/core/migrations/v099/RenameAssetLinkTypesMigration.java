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

  private static final Map<String, String> LEGACY_LINK_LABEL_BY_TYPE = Map.of(
      "data-source", "Data Source",
      "measurement", "Data Lake Storage"
  );

  private static final Map<String, String> RENAMED_LINK_LABEL_BY_TYPE = Map.of(
      "data-source", "Data Stream",
      "measurement", "Dataset"
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
          .anyMatch(this::requiresRename);
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    List<Map<String, Object>> assetLinkTypes = genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE);
    for (Map<String, Object> assetLinkType : assetLinkTypes) {
      if (!requiresRename(assetLinkType)) {
        continue;
      }

      assetLinkType.put(FIELD_LINK_LABEL, RENAMED_LINK_LABEL_BY_TYPE.get(assetLinkType.get(FIELD_LINK_TYPE)));
      genericStorage.update(String.valueOf(assetLinkType.get(FIELD_ID)), mapper.writeValueAsString(assetLinkType));
    }
  }

  private boolean requiresRename(Map<String, Object> assetLinkType) {
    String legacyLabel = LEGACY_LINK_LABEL_BY_TYPE.get(assetLinkType.get(FIELD_LINK_TYPE));
    return legacyLabel != null && legacyLabel.equals(assetLinkType.get(FIELD_LINK_LABEL));
  }

  @Override
  public String getDescription() {
    return "Renaming asset link types for data streams and datasets";
  }
}
