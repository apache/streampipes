package org.streampipes.pe.sources.hella.config;

import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportProtocol;

public class ProaSenseSettings {

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
