package de.fzi.cep.sepa.esper.output.topx;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.aggregate.count.Count;
import de.fzi.cep.sepa.esper.aggregate.count.CountParameter;
import de.fzi.cep.sepa.esper.aggregate.count.TimeScale;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StandardTransportFormat;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ListOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class TopXController extends EsperDeclarer<TopXParameter>{

	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
	
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(XSD._integer.toString(), "", "", Utils.createList(URI.create("http://schema.org/Number")));
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SEPA desc = new SEPA("/sepa/topX", "Top-X", "Aggregates an event stream and outputs a list of events order by a given property", "", "/sepa/topX", domains);
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		ListOutputStrategy outputStrategy = new ListOutputStrategy();
		outputStrategy.setPropertyName("list");
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("direction", "Direction: ");
		operation.addOption(new Option("Ascending"));
		operation.addOption(new Option("Descending"));
		
		staticProperties.add(operation);
		
		MappingProperty mp = new MappingPropertyUnary(URI.create(e1.getElementName()), "sortBy", "Sort by: ");
		staticProperties.add(mp);
		staticProperties.add(new FreeTextStaticProperty("topx", "Number of events: "));
		
		MappingProperty unique = new MappingPropertyNary("unique", "Unique properties: ");
		staticProperties.add(unique);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		
		EventStream inputStream = sepa.getInputStreams().get(0);
		
		EventGrounding inputGrounding = inputStream.getEventGrounding();
		EventGrounding outputGrounding = sepa.getOutputStream().getEventGrounding();
		String topicPrefix = "topic://";
		
		String inName = topicPrefix + inputGrounding.getTopicName();
		String outName = topicPrefix + outputGrounding.getTopicName();
		
		//SEPAUtils.getMapp
		
		
		String sortBy = SEPAUtils.getMappingPropertyName(sepa,
				"sortBy", true);
		
		int limit = Integer.parseInt(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "topx"))).getValue());
	
		String direction = SEPAUtils.getOneOfProperty(sepa,
				"direction");
		
		List<String> uniqueProperties = SEPAUtils.getMultipleMappingPropertyNames(sepa,
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
			return invokeEPRuntime(staticParam, TopX::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
