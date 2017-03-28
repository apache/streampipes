package de.fzi.cep.sepa.model.client.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Message {

	private boolean success;
	private String elementName;
	
	private List<Notification> notifications;
	
	public Message(boolean success){
		this.success = success;
		this.notifications = null;
	}
	
	public Message(boolean success, List<Notification> notifications) {
		super();
		this.success = success;
		this.notifications = notifications;
	}
	
	public Message(boolean success, List<Notification> notifications, String elementName) {
		this(success, notifications);
		this.elementName = elementName;
	}
	
	
	public Message(boolean success, Notification...notifications)
	{
		this.success = success;
		this.notifications = new ArrayList<Notification>();
		this.notifications.addAll(Arrays.asList(notifications));
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
	
	public boolean addNotification(Notification notification)
	{
		return notifications.add(notification);
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	
	
	
}
