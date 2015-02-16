package de.fzi.cep.sepa.esper.aggregate.avg;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;

public class AggregationController extends EsperDeclarer<AggregationParameter> {

	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
	
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventProperty e1 = new EventProperty("name", "description", "a", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		e1.setPropertyType(XSD._integer.toString());
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SEPA desc = new SEPA("/sepa/aggregation", "Aggregation", "Performs different aggregation", "", "/sepa/aggregation", domains);
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventProperty(XSD._double.toString(),
				"averageValue", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		FixedOutputStrategy outputStrategy = new FixedOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Operation");
		operation.addOption(new Option("Average"));
		operation.addOption(new Option("Sum"));
		operation.addOption(new Option("Min"));
		operation.addOption(new Option("Max"));
		operation.addOption(new Option("Count"));
		staticProperties.add(operation);
		
		MappingProperty mp = new MappingProperty(URI.create(e1.getElementName()), "groupBy", "group stream by: ");
		MappingProperty agg = new MappingProperty(URI.create(e1.getElementName()), "aggregate", "aggregate property: ");
		staticProperties.add(mp);
		staticProperties.add(agg);
		
		staticProperties.add(new FreeTextStaticProperty("outputEvery", "output values every (seconds)"));
		staticProperties.add(new FreeTextStaticProperty("timeWindow", "Time window size (seconds)"));
		desc.setStaticProperties(staticProperties);
		
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
		
		
		String groupBy = SEPAUtils.getMappingPropertyName(sepa,
				"groupBy");
		
		String aggregate = SEPAUtils.getMappingPropertyName(sepa,
				"aggregate");
		
		int outputEvery = Integer.parseInt(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "outputEvery"))).getValue());
		
		int timeWindowSize = Integer.parseInt(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "timeWindow"))).getValue());
	
		String aggregateOperation = SEPAUtils.getOneOfProperty(sepa,
				"operation");
		
		AggregationType aggregationType;
		
		if (aggregateOperation.equals("Average")) aggregationType = AggregationType.AVG;
		else if (aggregateOperation.equals("Sum")) aggregationType = AggregationType.SUM;
		else if (aggregateOperation.equals("Min")) aggregationType = AggregationType.MIN;
		else if (aggregateOperation.equals("Max")) aggregationType = AggregationType.MAX;
		else aggregationType = AggregationType.COUNT;
		
		
		AggregationParameter staticParam = new AggregationParameter(inName, outName, inputStream.getEventSchema().toPropertyList(), sepa.getOutputStream().getEventSchema().toPropertyList(), aggregationType, outputEvery, groupBy, aggregate, timeWindowSize);
		
		
		try {
			return runEngine(staticParam, Aggregation::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}

}