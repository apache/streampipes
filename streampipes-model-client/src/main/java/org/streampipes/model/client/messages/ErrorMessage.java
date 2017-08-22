package org.streampipes.model.client.messages;

import java.util.List;

public class ErrorMessage extends Message {

	public ErrorMessage(Notification...notifications) {
		super(false, notifications);
	}	
	
	public ErrorMessage(List<Notification> notifications) {
		super(false, notifications.toArray(new Notification[0]));
	}
	
	public ErrorMessage(String elementName, List<Notification> notifications) {
		super(false, notifications, elementName);
	}
}
