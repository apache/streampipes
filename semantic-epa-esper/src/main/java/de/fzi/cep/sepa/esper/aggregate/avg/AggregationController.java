package de.fzi.cep.sepa.esper.aggregate.avg;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.Domain;
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
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.impl.quality.Latency;
import de.fzi.cep.sepa.model.impl.quality.Precision;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class AggregationController extends EpDeclarer<AggregationParameter> {

	@Override
	public SepaDescription declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
	
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventPropertyPrimitive e1 = new EventPropertyPrimitive(Utils.createURI("http://schema.org/Number"));
		
		List<EventPropertyQualityRequirement> requiresPropertyQualities = new ArrayList<EventPropertyQualityRequirement>();
		requiresPropertyQualities.add(new EventPropertyQualityRequirement(new Precision(1), new Precision(10)));
		requiresPropertyQualities.add(new EventPropertyQualityRequirement(null, new Latency(200)));
		e1.setRequiresEventPropertyQualities(requiresPropertyQualities);
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("/sepa/aggregation", "Aggregation", "Performs different aggregation functions", "", "/sepa/aggregation", domains);
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Aggregation_Icon_HQ.png");
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		
		// add constraints to the event stream
		List<EventStreamQualityRequirement> requiredStreamQualities = new ArrayList<EventStreamQualityRequirement>();
		requiredStreamQualities.add(new EventStreamQualityRequirement(new Frequency(10), new Frequency(100)));
		stream1.setRequiresEventStreamQualities(requiredStreamQualities);
		
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._double.toString(),
				"averageValue", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Operation");
		operation.addOption(new Option("Average"));
		operation.addOption(new Option("Sum"));
		operation.addOption(new Option("Min"));
		operation.addOption(new Option("Max"));
		staticProperties.add(operation);
		
		MappingProperty mp = new MappingPropertyNary("groupBy", "group stream by: ");
		MappingProperty agg = new MappingPropertyUnary(URI.create(e1.getElementName()), "aggregate", "aggregate property: ");
		staticProperties.add(mp);
		staticProperties.add(agg);
		
		staticProperties.add(new FreeTextStaticProperty("outputEvery", "output values every (seconds)"));
		staticProperties.add(new FreeTextStaticProperty("timeWindow", "Time window size (seconds)"));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SepaInvocation sepa) {
		
		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa,
				"groupBy", true);
		
		String aggregate = SepaUtils.getMappingPropertyName(sepa,
				"aggregate");
		
		System.out.println("AGG: " +aggregate);
		
		int outputEvery = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(sepa, "outputEvery"))).getValue());
		
		int timeWindowSize = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(sepa, "timeWindow"))).getValue());
	
		String aggregateOperation = SepaUtils.getOneOfProperty(sepa,
				"operation");
		
		System.out.println("AGGOP: " +aggregateOperation);
		
		AggregationType aggregationType;
		
		if (aggregateOperation.equals("Average")) aggregationType = AggregationType.AVG;
		else if (aggregateOperation.equals("Sum")) aggregationType = AggregationType.SUM;
		else if (aggregateOperation.equals("Min")) aggregationType = AggregationType.MIN;
		else aggregationType = AggregationType.MAX;
		
		
		AggregationParameter staticParam = new AggregationParameter(sepa, aggregationType, outputEvery, groupBy, aggregate, timeWindowSize);
		
		
		try {
			return invokeEPRuntime(staticParam, Aggregation::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}