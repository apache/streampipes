package de.fzi.cep.sepa.actions.messaging.jms;

public interface IMessageListener {

	public void onEvent(String json);
	
}
