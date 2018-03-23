/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.couchdb.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.Notification;
import org.streampipes.storage.api.INotificationStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationStorageImpl extends AbstractDao<Notification> implements
        INotificationStorage {

  Logger LOG = LoggerFactory.getLogger(NotificationStorageImpl.class);

  public NotificationStorageImpl() {
    super(Utils::getCouchDbNotificationClient, Notification.class);
  }

  @Override
  public Notification getNotification(String notificationId) {
    return findWithNullIfEmpty(notificationId);
  }

  @Override
  public List<Notification> getAllNotifications() {
    return findAll();
  }

  @Override
  public boolean addNotification(Notification notification) {
    persist(notification);
    return true;
  }

  @Override
  public boolean changeNotificationStatus(String notificationId) {
    Notification msg = getNotification(notificationId);
    msg.setRead(!msg.isRead());

    return update(msg);
  }

  @Override
  public boolean deleteNotification(String notificationId) {
    return delete(notificationId);
  }

  @Override
  public List<Notification> getUnreadNotifications() {
    List<Notification> msgs = findAll();

    return msgs
            .stream()
            .filter(m -> !m.isRead())
            .collect(Collectors.toList());
  }
}
