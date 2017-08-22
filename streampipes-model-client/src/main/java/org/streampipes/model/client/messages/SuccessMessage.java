package org.streampipes.model.client.messages;

import java.util.List;

public class SuccessMessage extends Message {

	public SuccessMessage(Notification... notifications) {
		super(true, notifications);
	}

	public SuccessMessage(List<Notification> notifications) {
		super(true, notifications.toArray(new Notification[0]));
	}
	
	public SuccessMessage(String elementName, List<Notification> notifications) {
		super(true, notifications, elementName);
	}
}
