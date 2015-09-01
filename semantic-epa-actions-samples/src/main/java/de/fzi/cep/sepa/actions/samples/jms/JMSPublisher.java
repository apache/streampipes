package de.fzi.cep.sepa.actions.samples.jms;

import javax.jms.JMSException;

import de.fzi.cep.sepa.actions.messaging.jms.ActiveMQPublisher;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;

public class JMSPublisher implements IMessageListener {

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
