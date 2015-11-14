package de.fzi.cep.sepa.esper.output.topx;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.ListOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class TopXController extends FlatEpDeclarer<TopXParameter>{

	@Override
	public SepaDescription declareModel() {

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(XSD._integer.toString(), "", "", Utils.createList(URI.create("http://schema.org/Number")));
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("sepa/topX", "Top-X", "Aggregates an event stream and outputs a list of events order by a given property");
		
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
		
		try {
			invokeEPRuntime(staticParam, TopX::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
