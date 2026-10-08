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

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Configuration of the system notification that administrators can show to all users.
 *
 * @param enabled whether an administrator has switched the notification on.
 * @param message the text shown to users, rendered in a single line.
 * @param type the kind of notification, for example a hint or a warning.
 * @param expiresAtMillis the point in time from which the notification is no longer shown,
 *     or {@code null} to show it until it is switched off.
 */
public record SystemNotificationConfig(boolean enabled,
                                       String message,
                                       SystemNotificationType type,
                                       Long expiresAtMillis) {

  public SystemNotificationConfig {
    message = Objects.requireNonNullElse(message, "");
    type = Objects.requireNonNullElse(type, SystemNotificationType.INFO);
  }

  /**
   * Creates the state in which users see no system notification.
   */
  public static SystemNotificationConfig disabled() {
    return new SystemNotificationConfig(
        false, "", SystemNotificationType.INFO, null);
  }

  /**
   * Decides whether users should see this notification at the given point in time.
   *
   * @param now the point in time to decide for.
   * @return {@code true} if the notification should be visible then, otherwise {@code false}.
   */
  public boolean shouldBeDisplayedAt(Instant now) {
    return enabled && !message.isBlank() && !hasExpiredAt(now);
  }

  /**
   * Checks whether the point at which the notification should disappear has been reached.
   * Without such a point it only disappears when an administrator switches it off.
   *
   * @param now the point in time to check against.
   * @return whether the notification should no longer be shown.
   */
  private boolean hasExpiredAt(Instant now) {
    return expiry()
        .map(expiry -> !now.isBefore(expiry))
        .orElse(false);
  }

  /**
   * Returns the point at which the notification stops being shown, or an empty result if no
   * such point exists.
   */
  private Optional<Instant> expiry() {
    return Optional.ofNullable(expiresAtMillis).map(Instant::ofEpochMilli);
  }
}
