package de.fzi.cep.sepa.commons.messaging;

public interface IMessagePublisher<T> {

	public void publish(T message);
	
}
