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

import org.apache.streampipes.model.Notification;
import org.apache.streampipes.model.NotificationCount;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/notifications")
public class NotificationResource extends AbstractAuthGuardedRestResource {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> addNotification(@RequestBody Notification notification) {
    getNotificationStorage().addNotification(notification);
    return ok();
  }

  @GetMapping(path = "/offset", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Notification>> getNotifications(@RequestParam("notificationType") String notTypeId,
          @RequestParam("offset") Integer offset, @RequestParam("count") Integer count) {
    return ok(getNotificationStorage().getAllNotifications(notTypeId, offset, count));
  }

  @GetMapping(path = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Notification>> getNotifications(@RequestParam("startTime") long startTime) {
    return ok(getNotificationStorage().getAllNotificationsFromTimestamp(startTime));
  }

  @GetMapping(path = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<NotificationCount> getUnreadNotificationsCount() {
    return ok(getNotificationStorage().getUnreadNotificationsCount(getAuthenticatedUserSid()));
  }

  @GetMapping(path = "/unread")
  public ResponseEntity<List<Notification>> getUnreadNotifications() {
    return ok(getNotificationStorage().getUnreadNotifications());
  }

  @DeleteMapping(path = "/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<? extends Message> deleteNotification(@PathVariable("notificationId") String notificationId) {
    boolean success = getNotificationStorage().deleteNotification(notificationId);
    if (success) {
      return ok(Notifications.success("Notification deleted"));
    } else {
      return ok(Notifications.error("Could not delete notification"));
    }

  }

  @PutMapping(path = "/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> modifyNotificationStatus(@PathVariable("notificationId") String notificationId) {
    boolean success = getNotificationStorage().changeNotificationStatus(notificationId);
    if (success) {
      return ok(Notifications.success("Ok"));
    } else {
      return ok(Notifications.error("Error"));
    }
  }
}
