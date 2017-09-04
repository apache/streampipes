package org.streampipes.pe.processors.esper.proasense.hella.shuttletime;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.FixedOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShuttleTimeController extends StandaloneEventProcessorDeclarerSingleton<ShuttleTimeParameters> {

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("shuttletime", "Shuttle Time", "Calculates the time a shuttle needs between the lacquering line and each moulding machine.");
		desc.setCategory(Arrays.asList(EpaType.ALGORITHM.name()));
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#montracEvent").build();
		EventProperty e2 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#shuttleId").build();
		EventProperty e3 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#montracLocationId").build();
		EventProperty e4 = PrimitivePropertyBuilder.createPropertyRestriction("http://schema.org/DateTime").build();
		eventProperties.add(e1);
		eventProperties.add(e2);
		eventProperties.add(e3);
		eventProperties.add(e4);
		
		EventStream stream1 = StreamBuilder
				.createStreamRestriction(EsperConfig.serverUrl +"/" + desc.getElementId())
				.schema(
						SchemaBuilder.create()
							.properties(eventProperties)
							.build()
						).build();
		desc.addEventStream(stream1);
		
		FixedOutputStrategy strategy = new FixedOutputStrategy();
		EventProperty p1 = EpProperties.stringEp("lacqueringLineId", "http://hella.de/hella#lacqueringLineId");
		EventProperty p2 = EpProperties.stringEp("mouldingMachineId", "http://hella.de/hella#mouldingMachineId");
		EventProperty p3 = EpProperties.stringEp("shuttleId", "http://hella.de/hella#shuttleId");
		EventProperty p4 = EpProperties.longEp("timeDifference", SO.Number);
		
		List<EventProperty> properties = Arrays.asList(p1, p2, p3, p4);
		
		strategy.setEventProperties(properties);
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(strategy);
		
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e3.getElementName()), "mapping-location",
				"Location: ", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "mapping-event",
				"Event: ", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "mapping-shuttle",
				"Shuttle: ", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e4.getElementName()), "mapping-time",
				"Timestamp: ", ""));
		
		desc.setStaticProperties(staticProperties);
		
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<ShuttleTimeParameters, EventProcessor<ShuttleTimeParameters>> onInvocation
					(SepaInvocation sepa) {
		List<String> selectProperties = new ArrayList<>();
		for (EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties()) {
			selectProperties.add(p.getRuntimeName());
		}

		String locationEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-location");
		String eventEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-event");
		String shuttleEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-shuttle");
		String timestampEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-time");

		ShuttleTimeParameters staticParam = new ShuttleTimeParameters(sepa, selectProperties, locationEventName, eventEventName, shuttleEventName, timestampEventName);

		return new ConfiguredEventProcessor<>(staticParam, ShuttleTime::new);
	}
}
