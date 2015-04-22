package de.fzi.cep.sepa.sources.samples.activemq;

public interface IMessageListener {

	public void onEvent(String json);
	
}
