package de.fzi.cep.sepa.rest.messages;

public class SuccessMessage extends Message {

	public SuccessMessage(Notification... notifications) {
		super(true, notifications);
	}
}
