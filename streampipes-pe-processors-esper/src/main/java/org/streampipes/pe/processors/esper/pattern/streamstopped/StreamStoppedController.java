package org.streampipes.pe.processors.esper.pattern.streamstopped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.FixedOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.wrapper.standalone.declarer.FlatEpDeclarer;
import org.streampipes.container.util.StandardTransportFormat;

public class StreamStoppedController extends FlatEpDeclarer<StreamStoppedParameter> {

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("streamStopped", "Stream Stopped Detector", "Detects when the stream stopped");
		desc.setCategory(Arrays.asList(EpaType.PATTERN_DETECT.name()));
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(),
				"topic", "", Utils.createURI("http://schema.org/Text"));
		EventProperty timestamp = new EventPropertyPrimitive(XSD._long.toString(),
				"timestamp", "", Utils.createURI("http://schema.org/Number"));

		FixedOutputStrategy outputStrategy = new FixedOutputStrategy(Utils.createList(outputProperty, timestamp));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(StaticProperties.stringFreeTextProperty("topic", "topic", ""));
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
	
		String topic = SepaUtils.getFreeTextStaticPropertyValue(sepa, "topic");
		StreamStoppedParameter staticParam = new StreamStoppedParameter(sepa, topic);

		return submit(staticParam, StreamStopped::new, sepa);

	}
}