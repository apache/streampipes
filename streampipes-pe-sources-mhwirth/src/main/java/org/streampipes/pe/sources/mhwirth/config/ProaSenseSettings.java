package org.streampipes.pe.sources.mhwirth.config;

import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportProtocol;

public class ProaSenseSettings {

	public static TransportProtocol standardProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				SourcesConfig.INSTANCE.getKafkaHost(),
				SourcesConfig.INSTANCE.getKafkaPort(),
				topicName, 
				SourcesConfig.INSTANCE.getZookeeperHost(),
				SourcesConfig.INSTANCE.getZookeeperPort());
		return protocol;
	}
}
