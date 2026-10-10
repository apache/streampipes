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

package org.apache.streampipes.rest;

import org.apache.streampipes.model.configuration.GeneralConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.configuration.SystemNotificationConfig;
import org.apache.streampipes.model.configuration.SystemNotificationType;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the system notification step of the {@link ResetManagement} class.
 */
class ResetManagementTest {

  private final ISpCoreConfigurationStorage configStorage = mock(ISpCoreConfigurationStorage.class);

  ResetManagement newResetManagement(SpCoreConfiguration storedConfig) {
    var resourceManager = mock(SpResourceManager.class);
    when(resourceManager.getCoreConfigurationStorage()).thenReturn(configStorage);
    when(configStorage.get()).thenReturn(storedConfig);
    return new ResetManagement(null, null, null, null, null, resourceManager, null, null);
  }

  /**
   * Checks that an enabled notification is switched off while the rest of the general
   * configuration is kept.
   */
  @Test
  void testDisableSystemNotification_enabledNotification_storesDisabledAndKeepsRest() {
    var generalConfig = new GeneralConfig();
    var storedConfig = new SpCoreConfiguration();
    generalConfig.setHostname("streampipes.example.org");
    generalConfig.setSystemNotification(new SystemNotificationConfig(
        true, "Maintenance tonight", SystemNotificationType.CRITICAL, null));
    storedConfig.setGeneralConfig(generalConfig);

    newResetManagement(storedConfig).disableSystemNotification();

    verify(configStorage).updateElement(storedConfig);
    assertEquals(SystemNotificationConfig.disabled(), generalConfig.getSystemNotification());
    assertEquals("streampipes.example.org", generalConfig.getHostname());
  }

  @Test
  void testDisableSystemNotification_nothingStored_storesNothing() {
    newResetManagement(null).disableSystemNotification();
    verify(configStorage, never()).updateElement(any());
  }

  @Test
  void testDisableSystemNotification_noGeneralConfig_storesNothing() {
    newResetManagement(new SpCoreConfiguration()).disableSystemNotification();
    verify(configStorage, never()).updateElement(any());
  }
}
