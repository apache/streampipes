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
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModifyAssetLinkIconMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(ModifyAssetLinkIconMigration.class);

  private final ObjectMapper mapper = new ObjectMapper();

  private static final String FIELD_ID = "_id";
  private static final String FIELD_LINK_TYPE = "linkType";
  private static final String FIELD_LINK_ICON = "linkIcon";

  private static final Map<String, String> ICON_BY_LINK_TYPE = Map.of(
      "file", "folder",
      "data-source", "sensors",
      "measurement", "dataset"
  );

  private final IGenericStorage genericStorage;

  public ModifyAssetLinkIconMigration() {
    this.genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();

  }

  @Override
  public boolean shouldExecute() {
    try {
      return genericStorage
          .findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE)
          .stream()
          .map(m -> m.get(FIELD_LINK_ICON))
          .filter(Objects::nonNull)
          .map(String::valueOf)
          .anyMatch(linkIcon -> linkIcon.equals("draft"));
    } catch (Exception e) {
      LOG.warn("Could not load asset link types");
      return false;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    final List<Map<String, Object>> assetLinkTypes;
    try {
      assetLinkTypes = genericStorage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE);
    } catch (Exception e) {
      LOG.warn("Could not load asset link types; migration not executed", e);
      return;
    }

    for (Map<String, Object> assetLinkType : assetLinkTypes) {
      Object linkTypeObj = assetLinkType.get(FIELD_LINK_TYPE);
      if (linkTypeObj == null) {
        continue;
      }

      String linkType = String.valueOf(linkTypeObj);
      String newIcon = ICON_BY_LINK_TYPE.get(linkType);
      if (newIcon == null) {
        continue;
      }

      assetLinkType.put(FIELD_LINK_ICON, newIcon);
      genericStorage.update((String) assetLinkType.get(FIELD_ID), toJson(assetLinkType));
    }
  }

  private String toJson(Map<String, Object> assetLinkType) throws JsonProcessingException {
    return mapper.writeValueAsString(assetLinkType);
  }

  @Override
  public String getDescription() {
    return "Migrating asset link icons";
  }
}
