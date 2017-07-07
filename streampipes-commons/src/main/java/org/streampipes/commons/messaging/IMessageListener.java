package org.streampipes.commons.messaging;

public interface IMessageListener<T> {

	void onEvent(T payload);
	
}
