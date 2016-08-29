package de.fzi.cep.sepa.rest.api;

public interface INotification {

	String getNotifications();
	
	String getUnreadNotifications();
	
	String deleteNotification(String notification);
	
	String modifyNotificationStatus(String notificationId);
}
