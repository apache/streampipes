package de.fzi.cep.sepa.runtime.flat.protocol.jms;

import de.fzi.cep.sepa.messaging.jms.ActiveMQConsumer;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;

import javax.jms.JMSException;

public class JmsConsumer extends Consumer<String> {

	private String brokerHostname;
	private int brokerPort;
	private String topic;
	
	private ActiveMQConsumer consumer;
	
	public JmsConsumer(String brokerHostname, int brokerPort, String topic, DatatypeDefinition dataType) {
		super(dataType);
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
		notify(dataType.unmarshal(json.getBytes()));
	}

}
