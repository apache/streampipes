package org.streampipes.pe.sinks.standalone.samples.jms;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.messaging.jms.ActiveMQConsumer;
import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;

public class JMSConsumer implements SemanticEventConsumerDeclarer{

	ActiveMQConsumer consumer;
	
	@Override
	public SecDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("jms", "JMS Publisher", "Publishes events to a JMS topic");
		desc.setIconUrl(ActionConfig.iconBaseUrl + "/jms_logo.png");
		desc.setCategory(Arrays.asList(EcType.FORWARD.name()));
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
	
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty timeWindow = new FreeTextStaticProperty("uri", "Broker URL", "");
		staticProperties.add(timeWindow);
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("topic", "Broker topic", "");
		staticProperties.add(duration);
		
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
		System.out.println("invoke");
		String consumerUrl = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname() + ":" +((JmsTransportProtocol)sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getPort();
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		String destinationUri = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "uri")).getValue();
		String topic = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "topic")).getValue();
		
		
		try {
			consumer = new ActiveMQConsumer(consumerUrl, consumerTopic);
			consumer.setListener(new JMSPublisher(destinationUri, topic));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		try {
			consumer.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isVisualizable() {
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		// TODO Auto-generated method stub
		return null;
	}

}
