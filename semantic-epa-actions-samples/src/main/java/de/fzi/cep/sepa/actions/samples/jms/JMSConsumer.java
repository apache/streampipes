package de.fzi.cep.sepa.actions.samples.jms;


import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.messaging.jms.ActiveMQConsumer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph;
import de.fzi.cep.sepa.model.util.SEPAUtils;

public class JMSConsumer implements SemanticEventConsumerDeclarer{

	ActiveMQConsumer consumer;
	
	@Override
	public SEC declareModel() {
		
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SEC desc = new SEC("/jms", "JMS Consumer", "Desc", "http://localhost:8080/img");
		
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
	
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty timeWindow = new FreeTextStaticProperty("uri", "Broker URL");
		staticProperties.add(timeWindow);
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("topic", "Broker topic");
		staticProperties.add(duration);
		
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public String invokeRuntime(SECInvocationGraph sec) {
		System.out.println("invoke");
		String consumerUrl = sec.getInputStreams().get(0).getEventGrounding().getUri() + ":" +sec.getInputStreams().get(0).getEventGrounding().getPort();
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTopicName();
		
		String destinationUri = ((FreeTextStaticProperty)SEPAUtils.getStaticPropertyByName(sec, "uri")).getValue();
		String topic = ((FreeTextStaticProperty)SEPAUtils.getStaticPropertyByName(sec, "topic")).getValue();
		
		
		try {
			consumer = new ActiveMQConsumer(consumerUrl, consumerTopic);
			consumer.setListener(new JMSPublisher(destinationUri, topic));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "success";
	}

	@Override
	public boolean detachRuntime(SECInvocationGraph sec) {
		try {
			consumer.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
