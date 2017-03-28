package de.fzi.cep.sepa.commons.messaging;

public interface IMessageListener<T> {

	void onEvent(T payload);
	
}
