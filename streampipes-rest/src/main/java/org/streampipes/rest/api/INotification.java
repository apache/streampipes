package org.streampipes.rest.api;

import javax.ws.rs.core.Response;

public interface INotification {

	Response getNotifications();

	Response getUnreadNotifications();

	Response deleteNotification(String notification);

	Response modifyNotificationStatus(String notificationId);
}
