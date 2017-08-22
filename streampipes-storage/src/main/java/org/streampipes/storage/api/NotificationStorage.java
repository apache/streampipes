package org.streampipes.storage.api;

import java.util.List;

import org.streampipes.model.client.messages.ProaSenseNotificationMessage;

public interface NotificationStorage {

	ProaSenseNotificationMessage getNotification(String notificationId);
	
	List<ProaSenseNotificationMessage> getAllNotifications();
	
	List<ProaSenseNotificationMessage> getUnreadNotifications();
	
	boolean addNotification(ProaSenseNotificationMessage notification);
	
	boolean changeNotificationStatus(String notificationId);
	
	boolean deleteNotification(String notificationId);

}
