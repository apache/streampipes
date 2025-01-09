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

import org.apache.streampipes.model.configuration.DefaultGeneralConfig;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;

public class MoveDirectoryConfigurationMigration implements Migration {

  private final ISpCoreConfigurationStorage storage;

  public MoveDirectoryConfigurationMigration() {
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore().getSpCoreConfigurationStorage();
  }

  @Override
  public boolean shouldExecute() {
    var config = storage.get();
    return config != null && config.getGeneralConfig().getAssetDir() == null;
  }

  @Override
  public void executeMigration() throws IOException {
    var config = storage.get();
    config.getGeneralConfig().setFilesDir(new DefaultGeneralConfig().makeFileLocation());
    config.getGeneralConfig().setAssetDir(new DefaultGeneralConfig().makeAssetLocation());

    config.setAssetDir(null);
    config.setFilesDir(null);

    storage.updateElement(config);
  }

  @Override
  public String getDescription() {
    return "Moving asset directory to general config";
  }
}
