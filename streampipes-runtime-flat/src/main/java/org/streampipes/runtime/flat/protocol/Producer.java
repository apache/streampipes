package org.streampipes.runtime.flat.protocol;

import org.streampipes.commons.messaging.IMessagePublisher;
import org.streampipes.runtime.flat.datatype.DatatypeDefinition;

public abstract class Producer implements IMessagePublisher<Object> {

	protected DatatypeDefinition dataType;
	
	public Producer(DatatypeDefinition dataType) {
		this.dataType = dataType;
	}
	
	public abstract void openProducer();
	
	public abstract void closeProducer();
}
