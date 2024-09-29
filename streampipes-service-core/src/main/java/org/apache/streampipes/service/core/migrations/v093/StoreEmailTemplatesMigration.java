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
package org.apache.streampipes.service.core.migrations.v093;

import org.apache.streampipes.model.configuration.DefaultEmailTemplateConfiguration;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;

public class StoreEmailTemplatesMigration implements Migration {

  private final ISpCoreConfigurationStorage storage;

  public StoreEmailTemplatesMigration() {
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore().getSpCoreConfigurationStorage();
  }

  @Override
  public boolean shouldExecute() {
    var config = storage.get();
    if (config == null) {
      return false;
    } else {
      return config.getEmailTemplateConfig() == null;
    }
  }

  @Override
  public void executeMigration() throws IOException {
    var config = storage.get();
    config.setEmailTemplateConfig(new DefaultEmailTemplateConfiguration().getDefaultTemplates());

    storage.updateElement(config);
  }

  @Override
  public String getDescription() {
    return "Moving email templates to configuration storage";
  }
}
