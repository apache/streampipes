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
    super(Utils.getCouchDbNotificationClient(), Notification.class);
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
