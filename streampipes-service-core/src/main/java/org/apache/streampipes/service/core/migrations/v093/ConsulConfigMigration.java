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

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class ConsulConfigMigration implements Migration {

  private final ISpCoreConfigurationStorage storage;

  public ConsulConfigMigration() {
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore().getSpCoreConfigurationStorage();
  }

  @Override
  public boolean shouldExecute() {
    return storage.getAll().size() == 0;
  }

  @Override
  public void executeMigration() {
    var currConf = BackendConfig.INSTANCE;
    var newConf = new SpCoreConfiguration();

    var messagingSettings = currConf.getMessagingSettings();
    newConf.setLocalAuthConfig(currConf.getLocalAuthConfig());
    newConf.setEmailConfig(currConf.getEmailConfig());
    newConf.setGeneralConfig(currConf.getGeneralConfig());

    messagingSettings.setJmsHost(currConf.getJmsHost());
    messagingSettings.setJmsPort(currConf.getJmsPort());
    messagingSettings.setMqttHost(currConf.getMqttHost());
    messagingSettings.setMqttPort(currConf.getMqttPort());
    messagingSettings.setNatsHost(currConf.getNatsHost());
    messagingSettings.setNatsPort(currConf.getNatsPort());
    messagingSettings.setKafkaHost(currConf.getKafkaHost());
    messagingSettings.setKafkaPort(currConf.getKafkaPort());
    messagingSettings.setPulsarUrl(currConf.getPulsarUrl());
    messagingSettings.setZookeeperHost(currConf.getZookeeperHost());
    messagingSettings.setZookeeperPort(currConf.getZookeeperPort());
    newConf.setAssetDir(currConf.getAssetDir());
    newConf.setFilesDir(currConf.getFilesDir());

    newConf.setMessagingSettings(messagingSettings);
    storage.createElement(newConf);
  }

  @Override
  public String getDescription() {
    return "Migrate Consul configurations to CouchDB";
  }
}
