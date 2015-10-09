package de.fzi.cep.sepa.commons.messaging;

public class ConsoleLoggingPublisher implements IMessagePublisher{

	@Override
	public void onEvent(String message) {
		System.out.println(message);
	}

}
