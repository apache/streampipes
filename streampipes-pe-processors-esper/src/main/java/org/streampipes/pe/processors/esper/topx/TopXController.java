package org.streampipes.pe.processors.esper.topx;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.ListOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyNary;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.wrapper.esper.config.EsperConfig;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopXController extends StandaloneEventProcessorDeclarer<TopXParameter> {

	@Override
	public SepaDescription declareModel() {

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.numberReq();
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("topX", "Top-X", "Aggregates an event stream and outputs a list of events order by a given property");
		desc.setCategory(Arrays.asList(EpaType.TRANSFORM.name()));
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		ListOutputStrategy outputStrategy = new ListOutputStrategy();
		outputStrategy.setPropertyName("list");
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("direction", "Direction: ", "");
		operation.addOption(new Option("Ascending"));
		operation.addOption(new Option("Descending"));
		
		staticProperties.add(operation);
		
		MappingProperty mp = new MappingPropertyUnary(URI.create(e1.getElementName()), "sortBy", "Sort by: ", "");
		staticProperties.add(mp);
		staticProperties.add(new FreeTextStaticProperty("topx", "Number of events: ", ""));
		
		MappingProperty unique = new MappingPropertyNary("unique", "Unique properties: ", "");
		staticProperties.add(unique);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
				
		String sortBy = SepaUtils.getMappingPropertyName(sepa,
				"sortBy", true);
		
		int limit = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "topx"))).getValue());
	
		String direction = SepaUtils.getOneOfProperty(sepa,
				"direction");
		
		List<String> uniqueProperties = SepaUtils.getMultipleMappingPropertyNames(sepa,
				"unique", true);
		
		OrderDirection orderDirection;
		
		if (direction.equals("Ascending")) orderDirection = OrderDirection.ASCENDING;
		else orderDirection = OrderDirection.DESCENDING;
		
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		TopXParameter staticParam = new TopXParameter(sepa, orderDirection, sortBy, "list", limit, uniqueProperties);

		return submit(staticParam, TopX::new);

	}
}
