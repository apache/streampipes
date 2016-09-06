package de.fzi.cep.sepa.commons.messaging;

public interface IMessagePublisher<T> {

	void publish(T message);
	
}
