package de.fzi.cep.sepa.sources.samples.activemq;

public interface IMessagePublisher {

	public void onEvent(String message);
	
}
