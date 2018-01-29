package org.streampipes.storage.couchdb.impl;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.Notification;
import org.streampipes.storage.api.NotificationStorage;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationStorageImpl extends Storage<Notification> implements NotificationStorage {

  Logger LOG = LoggerFactory.getLogger(NotificationStorageImpl.class);

  public NotificationStorageImpl() {
    super(Notification.class);
  }

  @Override
  public Notification getNotification(String notificationId) {
    return getWithNullIfEmpty(notificationId);
  }

  @Override
  public List<Notification> getAllNotifications() {
    return getAll();
  }

  @Override
  public boolean addNotification(Notification notification) {
    add(notification);
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
    List<Notification> msgs = getAll();

    return msgs
            .stream()
            .filter(m -> !m.isRead())
            .collect(Collectors.toList());
  }

  @Override
  protected CouchDbClient getCouchDbClient() {
    return Utils.getCouchDbNotificationClient();
  }
}
