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

import org.apache.streampipes.model.configuration.DefaultExportProviderConfig;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;

public class AddDefaultExportProviderMigration implements Migration {

  private final ISpCoreConfigurationStorage storage = StorageDispatcher.INSTANCE.getNoSqlStore()
      .getSpCoreConfigurationStorage();

  @Override
  public boolean shouldExecute() {

    try {

      boolean shouldExecute = storage.get().getExportProviderSettings() == null  || storage.get().getExportProviderSettings().isEmpty();

      return shouldExecute;
    } catch (Exception e) {
      return true;
    }

  }

  @Override
  public void executeMigration() throws IOException {

    var coreCfg = storage.get();

    coreCfg.setExportProviderSettings(new DefaultExportProviderConfig().make());

    storage.updateElement(coreCfg);

  }

  @Override
  public String getDescription() {
    return "Migrating SPCoreConfiguration to include the default folder.";
  }
}
