package de.fzi.cep.sepa.sources.samples.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class SampleSettings {

	public static TransportProtocol kafkaProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				ClientConfiguration.INSTANCE.getKafkaHost(), 
				ClientConfiguration.INSTANCE.getKafkaPort(), 
				topicName, 
				ClientConfiguration.INSTANCE.getZookeeperHost(), 
				ClientConfiguration.INSTANCE.getZookeeperPort());
		return protocol;
	}
	
	public static TransportProtocol jmsProtocol(String topicName)
	{
		JmsTransportProtocol protocol = new JmsTransportProtocol(
				ClientConfiguration.INSTANCE.getJmsHost(), ClientConfiguration.INSTANCE.getJmsPort(), topicName);
		return protocol;
	}
}
