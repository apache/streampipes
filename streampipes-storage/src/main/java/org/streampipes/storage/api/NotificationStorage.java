package org.streampipes.storage.api;

import org.streampipes.model.Notification;

import java.util.List;

public interface NotificationStorage {

	Notification getNotification(String notificationId);
	
	List<Notification> getAllNotifications();
	
	List<Notification> getUnreadNotifications();
	
	boolean addNotification(Notification notification);
	
	boolean changeNotificationStatus(String notificationId);
	
	boolean deleteNotification(String notificationId);

}
