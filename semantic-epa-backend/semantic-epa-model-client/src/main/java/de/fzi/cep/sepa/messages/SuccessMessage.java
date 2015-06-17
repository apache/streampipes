package de.fzi.cep.sepa.messages;

import java.util.List;

public class SuccessMessage extends Message {

	public SuccessMessage(Notification... notifications) {
		super(true, notifications);
	}

	public SuccessMessage(List<Notification> notifications) {
		super(true, notifications.toArray(new Notification[0]));
	}
}
