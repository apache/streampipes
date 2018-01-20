package org.streampipes.pe.processors.esper.proasense.hella.minshuttletime;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MinShuttleTimeController extends StandaloneEventProcessorDeclarerSingleton<MinShuttleTimeParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		
		DataProcessorDescription desc = new DataProcessorDescription("minshuttletime", "Minimum Shuttle Time", "Issues an alert if the minimum shuttle time is reached.");
		desc.setCategory(Arrays.asList(DataProcessorType.ALGORITHM.name()));
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#lacqueringLineId").build();
		EventProperty e2 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#shuttleId").build();
		EventProperty e3 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#mouldingMachineId").build();
		EventProperty e4 = PrimitivePropertyBuilder.createPropertyRestriction("http://schema.org/Number").build();
		eventProperties.add(e1);
		eventProperties.add(e2);
		eventProperties.add(e3);
		eventProperties.add(e4);
		
		SpDataStream stream1 = StreamBuilder
				.createStreamRestriction(EsperConfig.serverUrl +"/" + desc.getElementId())
				.schema(
						SchemaBuilder.create()
							.properties(eventProperties)
							.build()
						).build();
		desc.addEventStream(stream1);
		
		FixedOutputStrategy strategy = new FixedOutputStrategy();
		EventProperty p1 = EpProperties.integerEp(Labels.empty(), "lacqueringLineId", "http://hella" +
						".de/hella#lacqueringLineId");
		EventProperty p2 = EpProperties.integerEp(Labels.empty(), "mouldingMachineId", "http://hella.de/hella#mouldingMachineId");
		EventProperty p4 = EpProperties.integerEp(Labels.empty(), "timeDifference", SO.Number);
		
		List<EventProperty> properties = Arrays.asList(p1, p2, p4);
		
		strategy.setEventProperties(properties);
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(strategy);
		
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "mapping-lacqueringLineId",
				"Lacquering Line: ", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e3.getElementName()), "mapping-mouldingMachineId",
				"Moulding Machine Id: ", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "mapping-shuttle",
				"Shuttle: ", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e4.getElementName()), "mapping-time",
				"Timestamp: ", ""));
		
		desc.setStaticProperties(staticProperties);
		
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<MinShuttleTimeParameters> onInvocation
					(DataProcessorInvocation sepa) {
		List<String> selectProperties = new ArrayList<>();
		for (EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties()) {
			selectProperties.add(p.getRuntimeName());
		}

		sepa.getInputStreams().get(0).getEventSchema().getEventProperties().forEach(ep -> System.out.println(ep.getRuntimeName() +", " +((EventPropertyPrimitive)ep).getRuntimeType()));

		String lacqueringLineIdEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-lacqueringLineId");
		String mouldingMachineIdEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-mouldingMachineId");
		String shuttleEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-shuttle");
		String timestampEventName = SepaUtils.getMappingPropertyName(sepa, "mapping-time");

		MinShuttleTimeParameters staticParam = new MinShuttleTimeParameters(sepa, selectProperties, lacqueringLineIdEventName, mouldingMachineIdEventName, shuttleEventName, timestampEventName);

		return new ConfiguredEventProcessor<>(staticParam, MinShuttleTime::new);
	}
}
