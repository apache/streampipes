package de.fzi.cep.sepa.esper.pattern.streamstopped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.builder.StaticProperties;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class StreamStoppedController extends FlatEpDeclarer<StreamStoppedParameter> {

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("sepa/streamStopped", "Stream Stopped Detector", "Detects when the stream stopped");
		desc.setEpaTypes(Arrays.asList(EpaType.PATTERN_DETECT.name()));
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(),
				"topic", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Text"));
		EventProperty timestamp = new EventPropertyPrimitive(XSD._long.toString(),
				"timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));

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
		
		try {
			invokeEPRuntime(staticParam, StreamStopped::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}