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

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.model.configuration.GeneralConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.configuration.SystemNotificationConfig;
import org.apache.streampipes.model.configuration.SystemNotificationType;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the implementation of the {@link SystemNotificationResource} class.
 */
class SystemNotificationResourceTest {

  private static final String MESSAGE = "Maintenance tonight";

  /**
   * Creates an enabled maintenance notification.
   */
  SystemNotificationConfig newMaintenance(Long expiresAt) {
    return new SystemNotificationConfig(
        true, MESSAGE, SystemNotificationType.CRITICAL, expiresAt);
  }

  /**
   * Creates a stored configuration that contains the given notification.
   */
  SpCoreConfiguration newStoredConfig(SystemNotificationConfig notification) {
    var generalConfig = new GeneralConfig();
    var storedConfig = new SpCoreConfiguration();
    generalConfig.setSystemNotification(notification);
    storedConfig.setGeneralConfig(generalConfig);
    return storedConfig;
  }

  SystemNotificationResource newResource(SpCoreConfiguration storedConfig) {
    var configStorage = mock(ISpCoreConfigurationStorage.class);
    when(configStorage.get()).thenReturn(storedConfig);
    return new SystemNotificationResource(configStorage);
  }

  /**
   * Checks that a fresh installation, where nothing is stored yet, leads to no notification
   * instead of a NullPointerException on this public endpoint.
   */
  @Test
  void testGetActiveNotification_nothingStored_returnsDisabled() {
    var resource = newResource(null);
    assertEquals(SystemNotificationConfig.disabled(), resource.getActiveNotification());
  }

  @Test
  void testGetActiveNotification_noGeneralConfig_returnsDisabled() {
    var resource = newResource(new SpCoreConfiguration());
    assertEquals(SystemNotificationConfig.disabled(), resource.getActiveNotification());
  }

  @Test
  void testGetActiveNotification_noStoredNotification_returnsDisabled() {
    var storedConfig = new SpCoreConfiguration();
    var resource = newResource(storedConfig);
    storedConfig.setGeneralConfig(new GeneralConfig());
    assertEquals(SystemNotificationConfig.disabled(), resource.getActiveNotification());
  }

  @Test
  void testGetActiveNotification_expiredNotification_returnsDisabled() {
    var expiry = Instant.now().minus(Duration.ofMinutes(1));
    var resource = newResource(newStoredConfig(newMaintenance(expiry.toEpochMilli())));
    assertEquals(SystemNotificationConfig.disabled(), resource.getActiveNotification());
  }

  @Test
  void testGetActiveNotification_activeNotification_returnsStoredNotification() {
    var notification = newMaintenance(null);
    var resource = newResource(newStoredConfig(notification));
    assertEquals(notification, resource.getActiveNotification());
  }
}
