package de.fzi.cep.sepa.rest.messages;

public class ErrorMessage extends Message {

	public ErrorMessage(Notification...notifications) {
		super(false, notifications);
	}	
}
