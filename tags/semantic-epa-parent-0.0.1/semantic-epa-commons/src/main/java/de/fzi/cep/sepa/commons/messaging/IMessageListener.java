package de.fzi.cep.sepa.commons.messaging;

public interface IMessageListener<T> {

	public void onEvent(T payload);
	
}
