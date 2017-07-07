package org.streampipes.wrapper.standalone.protocol;

import org.streampipes.commons.messaging.IMessagePublisher;
import org.streampipes.wrapper.standalone.datatype.DatatypeDefinition;

public abstract class Producer implements IMessagePublisher<Object> {

	protected DatatypeDefinition dataType;
	
	public Producer(DatatypeDefinition dataType) {
		this.dataType = dataType;
	}
	
	public abstract void openProducer();
	
	public abstract void closeProducer();
}
