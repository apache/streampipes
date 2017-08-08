package org.streampipes.pe.sinks.standalone.samples;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.kafka.StreamPipesKafkaConsumer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecInvocation;

import java.util.ArrayList;
import java.util.List;


public abstract class ActionController implements SemanticEventConsumerDeclarer {

	protected StreamPipesKafkaConsumer kafkaConsumer;

	protected void startKafkaConsumer(String kafkaUrl, String topic, EventListener<byte[]> eventListener) {
		kafkaConsumer = new StreamPipesKafkaConsumer(kafkaUrl, topic, eventListener);
		Thread thread = new Thread(kafkaConsumer);
		thread.start();
	}

	protected void stopKafkaConsumer() {
		if (kafkaConsumer != null) {
			kafkaConsumer.close();
		}
	}
	
	protected String createWebsocketUri(SecInvocation sec)
	{
		return getEventGrounding(sec).getTransportProtocol().getBrokerHostname().replace("tcp",  "ws") + ":61614";
	}
	
	protected String extractTopic(SecInvocation sec)
	{
		return "/topic/" +getEventGrounding(sec).getTransportProtocol().getTopicName();
	}
	
	protected String createJmsUri(SecInvocation sec)
	{
		return getEventGrounding(sec).getTransportProtocol().getBrokerHostname() + ":" +((JmsTransportProtocol)getEventGrounding(sec).getTransportProtocol()).getPort();
	}
	
	protected String createKafkaUri(SecInvocation sec)
	{
		return getEventGrounding(sec).getTransportProtocol().getBrokerHostname() + ":" +((KafkaTransportProtocol)getEventGrounding(sec).getTransportProtocol()).getZookeeperPort();
	}
	
	private EventGrounding getEventGrounding(SecInvocation sec)
	{
		return sec.getInputStreams().get(0).getEventGrounding();
	}
	
	protected String[] getColumnNames(List<EventProperty> eventProperties)
	{
		List<String> result = new ArrayList<>();
		for(EventProperty p : eventProperties)
		{
			result.add(p.getRuntimeName());
		}
		return result.toArray(new String[0]);
	}
}
