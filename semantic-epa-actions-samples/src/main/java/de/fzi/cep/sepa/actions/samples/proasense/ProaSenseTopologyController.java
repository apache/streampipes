package de.fzi.cep.sepa.actions.samples.proasense;

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
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

public class ProaSenseTopologyController implements SemanticEventConsumerDeclarer {

	ActiveMQConsumer consumer;
	
	@Override
	public SecDescription declareModel() {
		
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("/storm", "ProaSense Storm", "Forward to ProaSense component", "http://localhost:8080/img");
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public String invokeRuntime(SecInvocation sec) {
		String consumerUrl = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getUri() + ":" +sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getPort();
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
	
		consumer = new ActiveMQConsumer(consumerUrl, consumerTopic);
		consumer.setListener(new ProaSenseTopologyPublisher(sec));
		
		return "success";
	}

	@Override
	public boolean detachRuntime(SecInvocation sec) {
		try {
			consumer.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
