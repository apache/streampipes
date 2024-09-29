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
package org.apache.streampipes.manager.health;

import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.configuration.SpCoreConfigurationStatus;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreServiceStatusManager {

  private static final Logger LOG = LoggerFactory.getLogger(CoreServiceStatusManager.class);

  private final ISpCoreConfigurationStorage storage;

  public CoreServiceStatusManager(ISpCoreConfigurationStorage storage) {
    this.storage = storage;
  }

  public boolean existsConfig() {
    return storage.exists();
  }

  public boolean isCoreReady() {
    return existsConfig() && storage.get().getServiceStatus() == SpCoreConfigurationStatus.READY;
  }

  public void updateCoreStatus(SpCoreConfigurationStatus status) {
    var config = storage.get();
    config.setServiceStatus(status);
    storage.updateElement(config);
    logService(config);
  }

  private void logService(SpCoreConfiguration coreConfig) {
    LOG.info("Core is now in {} state", coreConfig.getServiceStatus());
  }
}
