package de.fzi.cep.sepa.sources.kd2.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class KafkaSettings {

	public static TransportProtocol standardProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				ClientConfiguration.INSTANCE.getKafkaHost(), 
				ClientConfiguration.INSTANCE.getKafkaPort(), 
				topicName, 
				ClientConfiguration.INSTANCE.getZookeeperHost(), 
				ClientConfiguration.INSTANCE.getZookeeperPort());
		return protocol;
	}
}
