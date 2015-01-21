package de.fzi.cep.sepa.actions.messaging.kafka;

import de.fzi.cep.sepa.actions.messaging.jms.IMessageListener;

public class KafkaTopic {

	private String topic;
	private IMessageListener listener;
		
	
	public KafkaTopic(String topic, IMessageListener listener) {
		super();
		this.topic = topic;
		this.listener = listener;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public IMessageListener getListener() {
		return listener;
	}
	public void setListener(IMessageListener listener) {
		this.listener = listener;
	}
	
	
}
