package org.streampipes.pe.sources.demonstrator.config;

import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportProtocol;

public class ProaSenseSettings {

	public static TransportProtocol standardProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				DemonstratorConfig.INSTANCE.getKafkaHost(),
				DemonstratorConfig.INSTANCE.getKafkaPort(),
				topicName, 
				DemonstratorConfig.INSTANCE.getZookeeperHost(),
				DemonstratorConfig.INSTANCE.getZookeeperPort());
		return protocol;
	}
}

