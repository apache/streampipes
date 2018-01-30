package org.streampipes.pe.processors.esper.pattern.streamstopped;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.vocabulary.XSD;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamStoppedController extends StandaloneEventProcessorDeclarerSingleton<StreamStoppedParameter> {

	@Override
	public DataProcessorDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);
		
		DataProcessorDescription desc = new DataProcessorDescription("streamStopped", "Stream Stopped Detector", "Detects when the stream stopped");
		desc.setCategory(Arrays.asList(DataProcessorType.PATTERN_DETECT.name()));
		
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
	public ConfiguredEventProcessor<StreamStoppedParameter> onInvocation
          (DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {
		String topic = SepaUtils.getFreeTextStaticPropertyValue(sepa, "topic");
		StreamStoppedParameter staticParam = new StreamStoppedParameter(sepa, topic);

		return new ConfiguredEventProcessor<>(staticParam, StreamStopped::new);

	}
}