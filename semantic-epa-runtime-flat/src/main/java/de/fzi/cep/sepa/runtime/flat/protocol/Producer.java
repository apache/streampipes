package de.fzi.cep.sepa.runtime.flat.protocol;

import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;

public abstract class Producer implements IMessagePublisher {

	public abstract void openProducer();
	
	public abstract void closeProducer();
}
