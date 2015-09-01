package de.fzi.cep.sepa.rest.api.v2;

public interface Notification {

	public String getNotifications();
	
	public String getUnreadNotifications();
	
	public String deleteNotification(String notification);
	
	public String modifyNotificationStatus(String notificationId);
}
