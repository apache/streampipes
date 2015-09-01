package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.messages.ProaSenseNotificationMessage;

public interface NotificationStorage {

	public ProaSenseNotificationMessage getNotification(String notificationId);
	
	public List<ProaSenseNotificationMessage> getAllNotifications();
	
	public void addNotification(ProaSenseNotificationMessage notification);
	
	public void changeNotificationStatus(String notificationId);
	
	public void deleteNotification(String notificationId);
}
