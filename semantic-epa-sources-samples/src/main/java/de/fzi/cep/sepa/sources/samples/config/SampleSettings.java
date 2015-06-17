package de.fzi.cep.sepa.sources.samples.config;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class SampleSettings {

	public static TransportProtocol kafkaProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				Configuration.getBrokerConfig().getKafkaHost(), 
				Configuration.getBrokerConfig().getKafkaPort(), 
				topicName, 
				Configuration.getBrokerConfig().getZookeeperHost(), 
				Configuration.getBrokerConfig().getZookeeperPort());
		return protocol;
	}
	
	public static TransportProtocol jmsProtocol(String topicName)
	{
		JmsTransportProtocol protocol = new JmsTransportProtocol(
				Configuration.getBrokerConfig().getJmsHost(), Configuration.getBrokerConfig().getJmsPort(), topicName);
		return protocol;
	}
}
