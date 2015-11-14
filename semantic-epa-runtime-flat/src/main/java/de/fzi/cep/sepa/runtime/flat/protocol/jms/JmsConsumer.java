package de.fzi.cep.sepa.runtime.flat.protocol.jms;

import javax.jms.JMSException;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQConsumer;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;

public class JmsConsumer extends Consumer implements IMessageListener{

	private String brokerHostname;
	private int brokerPort;
	private String topic;
	
	private ActiveMQConsumer consumer;
	
	public JmsConsumer(String brokerHostname, int brokerPort, String topic) {
		this.brokerHostname = brokerHostname;
		this.brokerPort = brokerPort;
		this.topic = topic;
	}
	
	@Override
	public void openConsumer() {
		consumer = new ActiveMQConsumer(brokerHostname +":" +brokerPort, topic);
		consumer.setListener(this);
	}

	@Override
	public void closeConsumer() {
		try {
			consumer.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onEvent(String json) {
		notify(json);
	}

}
