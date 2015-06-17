package de.fzi.cep.sepa.commons.messaging;

public interface IMessagePublisher {

	public void onEvent(String message);
	
}
