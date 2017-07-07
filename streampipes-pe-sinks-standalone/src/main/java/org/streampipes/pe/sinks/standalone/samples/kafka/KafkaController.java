package org.streampipes.pe.sinks.standalone.samples.kafka;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.impl.staticproperty.SupportedProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KafkaController extends ActionController {

	@Override
	public SecDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("kafka", "Kafka Publisher", "Forwards an event to a Kafka Broker");
		desc.setIconUrl(ActionConfig.iconBaseUrl + "/kafka_logo.png");
		desc.setCategory(Arrays.asList(EcType.FORWARD.name()));
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
			
			startKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(), consumerTopic,
					new KafkaPublisher(new StreamPipesKafkaProducer(kafkaHost + ":" +kafkaPort, topic)));

			
			//consumer.setListener(new ProaSenseTopologyPublisher(sec));
		    String pipelineId = sec.getCorrespondingPipeline();
            return new Response(pipelineId, true);
	}


    @Override
    public Response detachRuntime(String pipelineId) {
        stopKafkaConsumer();
        return new Response(pipelineId, true);
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
