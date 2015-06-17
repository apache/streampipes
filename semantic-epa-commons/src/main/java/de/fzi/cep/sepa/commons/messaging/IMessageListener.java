package de.fzi.cep.sepa.commons.messaging;

public interface IMessageListener {

	public void onEvent(String json);
	
}
