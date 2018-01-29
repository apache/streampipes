package org.streampipes.manager.monitoring.runtime;

import org.streampipes.commons.messaging.IMessageListener;
import org.streampipes.commons.messaging.IMessagePublisher;

public interface ProtocolHandler {

	IMessagePublisher getPublisher();
	IMessageListener getConsumer();
	
}
