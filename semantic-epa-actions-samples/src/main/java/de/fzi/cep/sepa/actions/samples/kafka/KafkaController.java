package de.fzi.cep.sepa.actions.samples.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class KafkaController implements SemanticEventConsumerDeclarer {

	KafkaConsumerGroup kafkaConsumerGroup;
	
	@Override
	public SecDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("kafka", "Kafka Publisher", "Forwards an event to a Kafka Broker");
		desc.setIconUrl(ActionConfig.iconBaseUrl + "/kafka_logo.png");
		desc.setEcTypes(Arrays.asList(EcType.FORWARD.name()));
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		SupportedProperty kafkaHost = new SupportedProperty("http://schema.org/kafkaHost", true);
		SupportedProperty kafkaPort = new SupportedProperty("http://schema.org/kafkaPort", true);
		
		List<SupportedProperty> supportedProperties = Arrays.asList(kafkaHost, kafkaPort);
		DomainStaticProperty dsp = new DomainStaticProperty("kafka-connection", "Kafka Connection Details", "Specifies connection details for the Apache Kafka broker", supportedProperties);
		
		staticProperties.add(dsp);
		
		FreeTextStaticProperty topic = new FreeTextStaticProperty("topic", "Broker topic", "");
		staticProperties.add(topic);
		
		desc.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();

		grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		desc.setSupportedGrounding(grounding);
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
			String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
			
			String topic = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "topic")).getValue();
		
			DomainStaticProperty dsp = SepaUtils.getDomainStaticPropertyBy(sec, "kafka-connection");
			String kafkaHost = SepaUtils.getSupportedPropertyValue(dsp, "http://schema.org/kafkaHost");
			int kafkaPort = Integer.parseInt(SepaUtils.getSupportedPropertyValue(dsp, "http://schema.org/kafkaPort"));
			
			kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), consumerTopic,
					new String[] {consumerTopic}, new KafkaPublisher(new ProaSenseInternalProducer(kafkaHost + ":" +kafkaPort, topic)));
			kafkaConsumerGroup.run(1);
			
			//consumer.setListener(new ProaSenseTopologyPublisher(sec));
			
			return null;
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		kafkaConsumerGroup.shutdown();
		return new Response("", true);
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
