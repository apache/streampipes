package de.fzi.cep.sepa.esper.aggregate.avg;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.builder.EpRequirements;
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
import de.fzi.cep.sepa.model.impl.quality.Accuracy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;

public class AggregationController extends FlatEpDeclarer<AggregationParameter> {

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventPropertyPrimitive e1 = EpRequirements.numberReq();

		List<EventPropertyQualityRequirement> numberQualities = new ArrayList<EventPropertyQualityRequirement>();
		numberQualities.add(new EventPropertyQualityRequirement(null, new Accuracy(20)));

		e1.setRequiresEventPropertyQualities(numberQualities);
		eventProperties.add(e1);

		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);

		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);

		SepaDescription desc = new SepaDescription("aggregation", "Aggregation",
				"Performs different aggregation functions");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Aggregation_Icon_HQ.png");
		desc.setCategory(Arrays.asList(EpaType.AGGREGATE.name()));
		// TODO check if needed
		stream1.setUri(EsperConfig.serverUrl + desc.getElementId());

		List<EventStreamQualityRequirement> eventStreamQualities = new ArrayList<EventStreamQualityRequirement>();
		eventStreamQualities.add(new EventStreamQualityRequirement(new Frequency(2), null));
		stream1.setRequiresEventStreamQualities(eventStreamQualities);

		desc.addEventStream(stream1);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();

		EventProperty outputProperty = new EventPropertyPrimitive(XSD._double.toString(), "averageValue", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);

		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Operation", "Aggregation operation type");
		operation.addOption(new Option("Average"));
		operation.addOption(new Option("Sum"));
		operation.addOption(new Option("Min"));
		operation.addOption(new Option("Max"));
		staticProperties.add(operation);

		MappingProperty mp = new MappingPropertyNary("groupBy", "Group by", "Partitions the incoming stream by the selected event properties");
		MappingProperty agg = new MappingPropertyUnary(URI.create(e1.getElementName()), "aggregate",
				"Property Selection", "Specifies the event property from your stream that should be aggregated.");
		staticProperties.add(mp);
		staticProperties.add(agg);
		FreeTextStaticProperty p = StaticProperties.integerFreeTextProperty("outputEvery", "Output Frequency", "output values every (seconds)");
		staticProperties.add(p);
		staticProperties.add(StaticProperties.integerFreeTextProperty("timeWindow", "Time Window Size", "Size of the time window in seconds"));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {

		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa, "groupBy", true);

		String aggregate = SepaUtils.getMappingPropertyName(sepa, "aggregate");

		int outputEvery = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(sepa, "outputEvery"))).getValue());

		int timeWindowSize = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(sepa, "timeWindow"))).getValue());

		String aggregateOperation = SepaUtils.getOneOfProperty(sepa, "operation");

		AggregationType aggregationType;

		if (aggregateOperation.equals("Average"))
			aggregationType = AggregationType.AVG;
		else if (aggregateOperation.equals("Sum"))
			aggregationType = AggregationType.SUM;
		else if (aggregateOperation.equals("Min"))
			aggregationType = AggregationType.MIN;
		else
			aggregationType = AggregationType.MAX;

		List<String> selectProperties = new ArrayList<>();
		for (EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties()) {
			selectProperties.add(p.getRuntimeName());
		}

		AggregationParameter staticParam = new AggregationParameter(sepa, aggregationType, outputEvery, groupBy,
				aggregate, timeWindowSize, selectProperties);

		return submit(staticParam, Aggregation::new, sepa);

	}
}