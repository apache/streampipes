package de.fzi.cep.sepa.sources.samples.config;

import de.fzi.cep.sepa.commons.config.ProaSenseConfig;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class ProaSenseSettings {

	public static TransportProtocol standardProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				ProaSenseConfig.kafkaHost, 
				ProaSenseConfig.kafkaPort, 
				topicName, 
				ProaSenseConfig.zookeeperHost, 
				ProaSenseConfig.zookeeperPort);
		return protocol;
	}
}
