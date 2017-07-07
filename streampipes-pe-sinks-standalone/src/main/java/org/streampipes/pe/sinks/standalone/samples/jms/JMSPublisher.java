package org.streampipes.pe.sinks.standalone.samples.jms;

import javax.jms.JMSException;

import org.streampipes.pe.sinks.standalone.messaging.jms.ActiveMQPublisher;
import org.streampipes.commons.messaging.IMessageListener;

public class JMSPublisher implements IMessageListener<String> {

	ActiveMQPublisher publisher;
	
	public JMSPublisher(String url, String topic) throws JMSException
	{
		publisher = new ActiveMQPublisher(url, topic);
	}

	@Override
	public void onEvent(String json) {
		publisher.send(json);
	}
}
