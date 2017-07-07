package de.fzi.cep.sepa.commons.messaging;

import java.io.Serializable;

public interface IMessagePublisher<T> extends Serializable {

	void publish(T message);
	
}
