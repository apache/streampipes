package org.streampipes.storage.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.streampipes.model.client.messages.ProaSenseNotificationMessage;
import org.streampipes.storage.api.NotificationStorage;
import org.streampipes.storage.util.Utils;

public class NotificationStorageImpl extends Storage<ProaSenseNotificationMessage> implements NotificationStorage {

    Logger LOG = LoggerFactory.getLogger(NotificationStorageImpl.class);

    public NotificationStorageImpl() {
        super(ProaSenseNotificationMessage.class);
    }

    @Override
    public ProaSenseNotificationMessage getNotification(String notificationId) {
        return getWithNullIfEmpty(notificationId);
    }

    @Override
    public List<ProaSenseNotificationMessage> getAllNotifications() {
        return getAll();
    }

    @Override
    public boolean addNotification(ProaSenseNotificationMessage notification) {
        add(notification);
        return true;
    }

    @Override
    public boolean changeNotificationStatus(String notificationId) {
        ProaSenseNotificationMessage msg = getNotification(notificationId);
        msg.setRead(!msg.isRead());

        return update(msg);
    }

    @Override
    public boolean deleteNotification(String notificationId) {
        return delete(notificationId);
    }

    @Override
    public List<ProaSenseNotificationMessage> getUnreadNotifications() {
        List<ProaSenseNotificationMessage> msgs = getAll();

        return msgs.stream().filter(m -> !m.isRead()).collect(Collectors.toList());
    }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbNotificationClient();
    }
}
