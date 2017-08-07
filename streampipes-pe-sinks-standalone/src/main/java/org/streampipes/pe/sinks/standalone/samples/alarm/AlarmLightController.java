package org.streampipes.pe.sinks.standalone.samples.alarm;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.commons.Utils;
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
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmLightController extends ActionController {

	@Override
	public SecDescription declareModel() {
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("alarm", "Alarm Light", "Switches the alarm light on or off.");
		desc.setCategory(Arrays.asList(EcType.ACTUATOR.name()));
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

		grounding.setTransportProtocol(new KafkaTransportProtocol(ActionConfig.INSTANCE.getKafkaHost(),
				ActionConfig.INSTANCE.getKafkaPort(), "", ActionConfig.INSTANCE.getZookeeperHost(),
				ActionConfig.INSTANCE.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		desc.setSupportedGrounding(grounding);
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation invocationGraph) {
		String selectedOption = SepaUtils.getOneOfProperty(invocationGraph, "state");
		String consumerTopic = invocationGraph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		AlarmLightParameters params = new AlarmLightParameters(selectedOption);

		startKafkaConsumer(ActionConfig.INSTANCE.getKafkaUrl(), consumerTopic, new AlarmLight(params));

		return new Response(invocationGraph.getElementId(), true);
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
