package org.streampipes.pe.sources.kd2.config;

import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportProtocol;

public class KafkaSettings {

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
