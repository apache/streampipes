package de.fzi.proasense.hella.config;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class ProaSenseSettings {

	public static TransportProtocol standardProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				Configuration.getInstance().getBrokerConfig().getKafkaHost(), 
				Configuration.getInstance().getBrokerConfig().getKafkaPort(), 
				topicName, 
				Configuration.getInstance().getBrokerConfig().getZookeeperHost(), 
				Configuration.getInstance().getBrokerConfig().getZookeeperPort());
		return protocol;
	}
}
