package de.fzi.cep.sepa.commons.messaging;

public class ConsoleLoggingPublisher implements IMessagePublisher<String> {

	@Override
	public void publish(String message) {
		System.out.println(message);
	}

}
