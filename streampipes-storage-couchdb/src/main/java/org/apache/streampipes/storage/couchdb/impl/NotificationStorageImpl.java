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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.Notification;
import org.apache.streampipes.model.NotificationCount;
import org.apache.streampipes.storage.api.INotificationStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationStorageImpl extends AbstractDao<Notification> implements
    INotificationStorage {

  Logger logger = LoggerFactory.getLogger(NotificationStorageImpl.class);

  public NotificationStorageImpl() {
    super(Utils::getCouchDbNotificationClient, Notification.class);
  }

  @Override
  public Notification getNotification(String notificationId) {
    return findWithNullIfEmpty(notificationId);
  }

  @Override
  public List<Notification> getAllNotifications(String notificationTypeId,
                                                Integer offset,
                                                Integer count) {
    List<JsonObject> notifications =
        getQuery(notificationTypeId)
            .includeDocs(true)
            .skip(offset)
            .limit(count)
            .query(JsonObject.class);

    return map(notifications);
  }

  @Override
  public List<Notification> getAllNotifications(String notificationTypeId) {
    List<JsonObject> notifications =
        getQuery(notificationTypeId)
            .includeDocs(true)
            .query(JsonObject.class);

    return map(notifications);
  }

  private View getQuery(String notificationTypeId) {
    return couchDbClientSupplier
        .get()
        .view("notificationtypes/notificationtypes")
        .startKey(Arrays.asList(notificationTypeId, "\ufff0"))
        .endKey(Arrays.asList(notificationTypeId, 0))
        .descending(true)
        .includeDocs(true);
  }

  @Override
  public List<Notification> getAllNotificationsFromTimestamp(long startTime) {

    return couchDbClientSupplier
        .get()
        .findDocs("{\"selector\": {\"createdAtTimestamp\": {\"$gt\": " + startTime + "}}}", Notification.class);
  }

  private List<Notification> map(List<JsonObject> jsonObjects) {
    Gson gson = couchDbClientSupplier.get().getGson();
    return jsonObjects
        .stream()
        .map(notification -> gson.fromJson(notification, Notification.class))
        .collect(Collectors.toList());
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

  @Override
  public NotificationCount getUnreadNotificationsCount(String username) {
    List<JsonObject> count =
        couchDbClientSupplier
            .get()
            .view("unread/unread")
            .key(username)
            .group(true)
            .query(JsonObject.class);

    if (count.size() > 0) {
      Integer countValue = count.get(0).get("value").getAsInt();
      return new NotificationCount(countValue);
    } else {
      return new NotificationCount(0);
    }
  }
}
