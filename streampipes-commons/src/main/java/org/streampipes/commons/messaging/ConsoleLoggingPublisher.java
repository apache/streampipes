package org.streampipes.commons.messaging;

public class ConsoleLoggingPublisher implements IMessagePublisher<String> {

	@Override
	public void publish(String message) {
		System.out.println(message);
	}

}
