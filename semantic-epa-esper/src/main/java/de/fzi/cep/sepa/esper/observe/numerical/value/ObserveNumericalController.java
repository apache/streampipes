package de.fzi.cep.sepa.esper.observe.numerical.value;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.commons.Utils;
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
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;

public class ObserveNumericalController extends FlatEpDeclarer<ObserveNumericalParameters> {

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventPropertyPrimitive e1 = EpRequirements.numberReq();
		eventProperties.add(e1);

		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);

		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);

		SepaDescription desc = new SepaDescription("observenumerical", "Observe Numerical",
				"Throws an alert when value exceeds a threshold value");
		desc.setEpaTypes(Arrays.asList(EpaType.FILTER.name()));

		desc.addEventStream(stream1);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();

		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(), "message", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/text"));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);

		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

		staticProperties.add(StaticProperties.doubleFreeTextProperty("threshold", "Threshold value", ""));
		
		OneOfStaticProperty valueLimit = new OneOfStaticProperty("value-limit", "Value Limit", "Is the threshold a minimum or maximum value");
		valueLimit.addOption(new Option("Upper Limit"));
		valueLimit.addOption(new Option("Under Limit"));
		staticProperties.add(valueLimit);

		MappingProperty toObserve = new MappingPropertyUnary(URI.create(e1.getElementName()), "number",
				"Number", "The number to be observed");
		staticProperties.add(toObserve);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {

		String valueLimit = SepaUtils.getOneOfProperty(invocationGraph, "value-limit");

		String outputProperty = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties().get(0).getRuntimeName();

		double threshold = Double.parseDouble(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "threshold")))
						.getValue());

		String value = SepaUtils.getMappingPropertyName(invocationGraph, "number");
		
		ObserveNumericalParameters params = new ObserveNumericalParameters(invocationGraph, valueLimit, threshold, value, outputProperty);


		try {
			invokeEPRuntime(params, ObserveNumerical::new, invocationGraph);
			return new Response(invocationGraph.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(invocationGraph.getElementId(), false, e.getMessage());
		}
	}

}
