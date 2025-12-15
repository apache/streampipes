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

public class ModifyAssetLinkTypesMigration implements Migration {

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
    var assetLinkTypes = storage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE);
    for (Map<String, Object> al : assetLinkTypes) {
      if (al.get("linkType").equals("data-view")) {
        al.put("linkType", "chart");
        al.put("linkLabel", "Chart");
        al.put("navPaths", List.of("dataexplorer", "chart"));
        al.put("linkIcon", "query_stats");
        al.put("linkQueryHint", "chart");
        storage.update(al.get("_id").toString(), new ObjectMapper().writeValueAsString(al));
      } else if (al.get("linkType").equals("dashboard")) {
        al.put("linkIcon", "dashboard");
        storage.update(al.get("_id").toString(), new ObjectMapper().writeValueAsString(al));
      }
    }
  }

  @Override
  public String getDescription() {
    return "Migrating asset link types for dashboard and data explorer";
  }
}
