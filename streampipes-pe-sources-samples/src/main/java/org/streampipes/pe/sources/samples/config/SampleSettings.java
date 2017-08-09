package org.streampipes.pe.sources.samples.config;

import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportProtocol;

public class SampleSettings {

	public static TransportProtocol kafkaProtocol(String topicName)
	{
		KafkaTransportProtocol protocol = new KafkaTransportProtocol(
				SourcesConfig.INSTANCE.getKafkaHost(),
				SourcesConfig.INSTANCE.getKafkaPort(),
				topicName, 
				SourcesConfig.INSTANCE.getZookeeperHost(),
				SourcesConfig.INSTANCE.getZookeeperPort());
		return protocol;
	}
	
	public static TransportProtocol jmsProtocol(String topicName)
	{
		JmsTransportProtocol protocol = new JmsTransportProtocol(
				SourcesConfig.INSTANCE.getJmsHost(), SourcesConfig.INSTANCE.getJmsPort(), topicName);
		return protocol;
	}
}
