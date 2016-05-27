package de.fzi.cep.sepa.esper.aggregate.count;

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
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class CountController extends FlatEpDeclarer<CountParameter>{

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("sepa/count", "Count Aggregation", "Performs an aggregation based on a given event property and outputs the number of occurrences.");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Counter_Icon_HQ.png");
		desc.setEpaTypes(Arrays.asList(EpaType.AGGREGATE.name()));
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		
		List<EventStreamQualityRequirement> eventStreamQualities = new ArrayList<EventStreamQualityRequirement>();
		Frequency minFrequency = new Frequency(5);
		Frequency maxFrequency = new Frequency(20);
		eventStreamQualities.add(new EventStreamQualityRequirement(minFrequency, maxFrequency));
		stream1.setRequiresEventStreamQualities(eventStreamQualities);
		
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._integer.toString(),
				"countValue", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("scale", "Time Window Scale", "");
		operation.addOption(new Option("Hours"));
		operation.addOption(new Option("Minutes"));
		operation.addOption(new Option("Seconds"));
		
		staticProperties.add(operation);
		
		MappingProperty mp = new MappingPropertyNary("groupBy", "Group stream by: ", "");
		staticProperties.add(mp);
		staticProperties.add(StaticProperties.integerFreeTextProperty("timeWindow", "Time Window Size", "The size of the time window"));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {		
		
		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa,
				"groupBy", true);
		
		int timeWindowSize = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "timeWindow"))).getValue());
	
		String scale = SepaUtils.getOneOfProperty(sepa,
				"scale");
		
		TimeScale timeScale;
		
		if (scale.equals("Hours")) timeScale = TimeScale.HOURS;
		else if (scale.equals("Minutes")) timeScale = TimeScale.MINUTES;
		else timeScale = TimeScale.SECONDS;
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		CountParameter staticParam = new CountParameter(sepa, timeWindowSize, groupBy, timeScale, selectProperties);
		
		try {
			invokeEPRuntime(staticParam, Count::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
