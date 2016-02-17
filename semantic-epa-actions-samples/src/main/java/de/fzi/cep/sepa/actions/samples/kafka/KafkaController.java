package de.fzi.cep.sepa.actions.samples.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
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
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
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
		
		FreeTextStaticProperty brokerUrl = new FreeTextStaticProperty("hostname", "Broker Hostname", "");
		staticProperties.add(brokerUrl);
		
		FreeTextStaticProperty port = new FreeTextStaticProperty("port", "Kafka Port", "");
		staticProperties.add(port);
		
		FreeTextStaticProperty topic = new FreeTextStaticProperty("topic", "Broker topic", "");
		staticProperties.add(topic);
		
		FreeTextStaticProperty addProperty = new FreeTextStaticProperty("additionalProperty", "Additionial Property Value", "");
		addProperty.setValueRequired(false);
		staticProperties.add(addProperty);
		
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
			
			String brokerHostname = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "hostname")).getValue();
			String topic = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "topic")).getValue();
			String port = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "port")).getValue();
			
			String pipelineId = ((FreeTextStaticProperty)SepaUtils.getStaticPropertyByInternalName(sec, "additionalProperty")).getValue();
			
			kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), consumerTopic,
					new String[] {consumerTopic}, new KafkaPublisher(new ProaSenseInternalProducer(brokerHostname + ":" +port, topic), pipelineId));
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
