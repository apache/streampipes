package de.fzi.cep.sepa.actions.samples.proasense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.messaging.jms.ActiveMQConsumer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class ProaSenseTopologyController implements SemanticEventConsumerDeclarer {

	ActiveMQConsumer consumer;
	private ProaSenseEventNotifier eventNotifier;
	
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
		
		SecDescription desc = new SecDescription("storm", "Online Analytics", "Processes event by Online Analytics");
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		desc.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();

		grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		desc.setSupportedGrounding(grounding);
		
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
		//String consumerUrl = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname() + ":" +((JmsTransportProtocol)sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getPort();
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		this.eventNotifier = new ProaSenseEventNotifier(consumerTopic);
		System.out.println(consumerTopic);
		//consumer = new ActiveMQConsumer(consumerUrl, consumerTopic);
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), consumerTopic,
				new String[] {consumerTopic}, new ProaSenseTopologyPublisher(sec, eventNotifier));
		kafkaConsumerGroup.run(1);
		
		//consumer.setListener(new ProaSenseTopologyPublisher(sec));
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		return new ProaSenseTopologyViewer(null, eventNotifier).generateHtml();
	}
	
	public String getClonedHtml(SecInvocation graph)
	{
		return new ProaSenseTopologyViewer(null, eventNotifier).generateHtml();
	}
}
