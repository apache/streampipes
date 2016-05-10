package de.fzi.cep.sepa.actions.alarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
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
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class AlarmLightController implements SemanticEventConsumerDeclarer {

	KafkaConsumerGroup kafkaConsumerGroup;
	
	@Override
	public SecDescription declareModel() {
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("alarm", "Alarm Light", "Switches the alarm light on or off.");
		desc.setEcTypes(Arrays.asList(EcType.ACTUATOR.name()));
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		OneOfStaticProperty onoff = new OneOfStaticProperty("state", "On/Off", "Specifies whether the alarm light should be turned on or off.");
	
		List<Option> options = new ArrayList<>();
		options.add(new Option("On"));
		options.add(new Option("Off"));
		
		onoff.setOptions(options);
		staticProperties.add(onoff);
		
		desc.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();

		grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		desc.setSupportedGrounding(grounding);
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation invocationGraph) {
		String selectedOption = SepaUtils.getOneOfProperty(invocationGraph, "state");
		String consumerTopic = invocationGraph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		AlarmLightParameters params = new AlarmLightParameters(selectedOption);
		
		kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), consumerTopic,
				new String[] {consumerTopic}, new AlarmLight(params));
		kafkaConsumerGroup.run(1);
		return new Response(invocationGraph.getElementId(), true);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		kafkaConsumerGroup.shutdown();
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
