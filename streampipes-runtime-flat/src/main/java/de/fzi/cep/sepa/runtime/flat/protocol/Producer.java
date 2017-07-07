package de.fzi.cep.sepa.runtime.flat.protocol;

import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;

public abstract class Producer implements IMessagePublisher<Object> {

	protected DatatypeDefinition dataType;
	
	public Producer(DatatypeDefinition dataType) {
		this.dataType = dataType;
	}
	
	public abstract void openProducer();
	
	public abstract void closeProducer();
}
