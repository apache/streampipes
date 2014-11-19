package de.fzi.cep.sepa.messages;

public class SuccessMessage extends Message {

	public SuccessMessage(Notification... notifications) {
		super(true, notifications);
	}
}
