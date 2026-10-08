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

package org.apache.streampipes.model.configuration;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the implementation of the {@link SystemNotificationConfig} class.
 */
class SystemNotificationConfigTest {

  private static final Instant NOW = Instant.parse("2026-09-11T12:00:00Z");

  private static final String EMPTY_MESSAGE = "";
  private static final String MESSAGE = "Maintenance tonight";

  /**
   * Creates a maintenance notification.
   */
  private static SystemNotificationConfig newMaintenance(boolean enabled, Long expiresAtMillis) {
    return new SystemNotificationConfig(
        enabled, MESSAGE, SystemNotificationType.CRITICAL, expiresAtMillis);
  }

  @Test
  void testSystemNotificationConfig_missingMessage_fallsBackToDefault() {
    var notification = new SystemNotificationConfig(
        true, null, SystemNotificationType.WARNING, null);
    assertEquals(EMPTY_MESSAGE, notification.message());
  }

  @Test
  void testSystemNotificationConfig_missingType_fallsBackToDefault() {
    var notification = new SystemNotificationConfig(
        true, MESSAGE, null, null);
    assertEquals(SystemNotificationType.INFO, notification.type());
  }

  @Test
  void testDisabled_notificationNotSet_isSwitchedOff() {
    var notification = SystemNotificationConfig.disabled();
    assertFalse(notification.enabled());
  }

  /**
   * Checks that an enabled notification with a message is shown if no expiry is set.
   */
  @Test
  void testShouldBeDisplayedAt_enabledWithoutExpiry_isShown() {
    var notification = newMaintenance(true, null);
    assertTrue(notification.shouldBeDisplayedAt(NOW));
  }

  /**
   * Checks that switching a notification off hides it, even if message and expiry would allow
   * showing it.
   */
  @Test
  void testShouldBeDisplayedAt_switchedOff_isHidden() {
    var notification = newMaintenance(false, null);
    assertFalse(notification.shouldBeDisplayedAt(NOW));
  }

  /**
   * Checks that a notification without visible text is hidden, so users never see an empty
   * notification.
   */
  @Test
  void testShouldBeDisplayedAt_blankMessage_isHidden() {
    var notification = new SystemNotificationConfig(
        true, "   ", SystemNotificationType.WARNING, null);
    assertFalse(notification.shouldBeDisplayedAt(NOW));
  }

  /**
   * Checks that a notification is still shown shortly before its expiry, so it does not
   * disappear too early.
   */
  @Test
  void testShouldBeDisplayedAt_oneMinuteBeforeExpiry_isShown() {
    var expiry = NOW.plus(Duration.ofMinutes(1));
    var notification = newMaintenance(true, expiry.toEpochMilli());
    assertTrue(notification.shouldBeDisplayedAt(NOW));
  }

  /**
   * Checks that a notification disappears exactly at its expiry.
   */
  @Test
  void testShouldBeDisplayedAt_exactlyAtExpiry_isHidden() {
    var notification = newMaintenance(true, NOW.toEpochMilli());
    assertFalse(notification.shouldBeDisplayedAt(NOW));
  }

  /**
   * Checks that an expired notification stays hidden, so users never see outdated
   * announcements.
   */
  @Test
  void testShouldBeDisplayedAt_oneMinuteAfterExpiry_isHidden() {
    var expiry = NOW.minus(Duration.ofMinutes(1));
    var notification = newMaintenance(true, expiry.toEpochMilli());
    assertFalse(notification.shouldBeDisplayedAt(NOW));
  }
}
