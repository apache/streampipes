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
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

/**
 * Answers whether there is a system notification to show right now, and which one.
 */
@RestController
@RequestMapping("/api/v2/system-notification")
public class SystemNotificationResource {

  private final ISpCoreConfigurationStorage configStorage;

  public SystemNotificationResource(ISpCoreConfigurationStorage configStorage) {
    this.configStorage = configStorage;
  }

  /**
   * Returns the notification that users should see at this moment, or a disabled one if none is
   * configured or the configured one has expired.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public SystemNotificationConfig getActiveNotification() {
    return findConfiguredNotification()
        .filter(notification -> notification.shouldBeDisplayedAt(Instant.now()))
        .orElseGet(SystemNotificationConfig::disabled);
  }

  /**
   * Reads the stored notification without checking whether it should be shown right now.
   *
   * @return the notification, or empty if none is stored, for example on a fresh installation
   *     or on installations set up before this feature existed.
   */
  private Optional<SystemNotificationConfig> findConfiguredNotification() {
    return Optional.ofNullable(configStorage.get())
        .map(SpCoreConfiguration::getGeneralConfig)
        .map(GeneralConfig::getSystemNotification);
  }
}
