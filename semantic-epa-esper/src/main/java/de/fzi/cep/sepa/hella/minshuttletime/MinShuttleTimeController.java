package de.fzi.cep.sepa.hella.minshuttletime;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class MinShuttleTimeController extends EpDeclarer<MinShuttleTimeParameters>{

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("sepa/minshuttletime", "Minimum Shuttle Time", "Issues an alert if the minimum shuttle time is reached.");
			
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#lacqueringLineId").build();
		EventProperty e2 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#shuttleId").build();
		EventProperty e3 = PrimitivePropertyBuilder.createPropertyRestriction("http://hella.de/hella#mouldingMachineId").build();
		EventProperty e4 = PrimitivePropertyBuilder.createPropertyRestriction("http://schema.org/Number").build();
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
		EventProperty p1 = EpProperties.integerEp("lacqueringLineId", "http://hella.de/hella#lacqueringLineId");
		EventProperty p2 = EpProperties.integerEp("mouldingMachineId", "http://hella.de/hella#mouldingMachineId");
		EventProperty p4 = EpProperties.integerEp("timeDifference", SO.Number);
		
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
	public Response invokeRuntime(SepaInvocation sepa) {
		
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
		
		try {
			invokeEPRuntime(staticParam, MinShuttleTime::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
