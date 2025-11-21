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


package org.apache.streampipes.service.core.migrations.v070;

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.manager.setup.tasks.CreateDefaultAssetTask;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.resource.management.CrudResourceManager;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;

public class CreateDefaultAssetMigration implements Migration {

  @Override
  public boolean shouldExecute() {
       try {
      CRUDStorage<SpAssetModel> assetStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAssetStorage();
      var resourceManager = new CrudResourceManager<>(assetStorage, SpAssetModel.class);
      List<SpAssetModel> assets = resourceManager.findAll();
      if (assets.size() > 0) {
        for (SpAssetModel asset : assets) {
    if ("default-asset".equals(asset.getAssetId())) {  
        return false;
    }
}       return true;
      } else {
        return true;
      }
    } catch (Exception e) {
      return true;
  }
}

  @Override
  public void executeMigration() {
    new CreateDefaultAssetTask().execute();
  }

  @Override
  public String getDescription() {
    return "Creating a default asset representation";
  }
}
