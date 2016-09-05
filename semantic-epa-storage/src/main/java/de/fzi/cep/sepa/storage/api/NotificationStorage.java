package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.messages.ProaSenseNotificationMessage;

public interface NotificationStorage {

	ProaSenseNotificationMessage getNotification(String notificationId);
	
	List<ProaSenseNotificationMessage> getAllNotifications();
	
	List<ProaSenseNotificationMessage> getUnreadNotifications();
	
	boolean addNotification(ProaSenseNotificationMessage notification);
	
	boolean changeNotificationStatus(String notificationId);
	
	boolean deleteNotification(String notificationId);

}
