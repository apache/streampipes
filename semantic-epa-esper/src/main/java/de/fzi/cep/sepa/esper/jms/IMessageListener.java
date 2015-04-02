package de.fzi.cep.sepa.esper.jms;

public interface IMessageListener {

	public void onEvent(String json);
	
}
