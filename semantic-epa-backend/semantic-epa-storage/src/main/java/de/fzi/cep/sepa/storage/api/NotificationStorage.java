package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.messages.ProaSenseNotificationMessage;

public interface NotificationStorage {

	public ProaSenseNotificationMessage getNotification(String notificationId);
	
	public List<ProaSenseNotificationMessage> getAllNotifications();
	
	public List<ProaSenseNotificationMessage> getUnreadNotifications();
	
	public boolean addNotification(ProaSenseNotificationMessage notification);
	
	public boolean changeNotificationStatus(String notificationId);
	
	public boolean deleteNotification(String notificationId);

}
