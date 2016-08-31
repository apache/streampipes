package de.fzi.cep.sepa.esper.observe.numerical.window;

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

public class ObserveNumericalWindowController extends FlatEpDeclarer<ObserveNumericalWindowParameters> {

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventPropertyPrimitive e1 = EpRequirements.numberReq();
		eventProperties.add(e1);

		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);

		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);

		SepaDescription desc = new SepaDescription("observenumericalvaluewindow", "Observe Numerical Value Window",
				"");
		desc.setCategory(Arrays.asList(EpaType.FILTER.name()));

		desc.addEventStream(stream1);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();

		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(), "message", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/text"));
		EventProperty averageProperty = new EventPropertyPrimitive(XSD._double.toString(), "average", "", Utils.createURI("http://schema.org/Number"));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty, averageProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);

		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

		staticProperties.add(StaticProperties.doubleFreeTextProperty("threshold", "Threshold value", ""));
		staticProperties.add(StaticProperties.integerFreeTextProperty("window-size", "Window size", ""));
		
		OneOfStaticProperty windowType = new OneOfStaticProperty("window-type", "Window Type", "");
		windowType.addOption(new Option("Time [sec]"));
		windowType.addOption(new Option("Event [#]"));
		staticProperties.add(windowType);
		
		OneOfStaticProperty valueLimit = new OneOfStaticProperty("value-limit", "Value Limit", "");
		valueLimit.addOption(new Option("Upper Limit"));
		valueLimit.addOption(new Option("Under Limit"));
		staticProperties.add(valueLimit);

		MappingProperty toObserve = new MappingPropertyUnary(URI.create(e1.getElementName()), "to-observe",
				"Value to Observe", "");
		staticProperties.add(toObserve);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {

		String valueLimit = SepaUtils.getOneOfProperty(invocationGraph, "value-limit");

		double threshold = Double.parseDouble(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "threshold")))
						.getValue());

		String toObserve = SepaUtils.getMappingPropertyName(invocationGraph, "to-observe");

		int windowSize = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "window-size")))
						.getValue());

		String messageName = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties()
				.get(0).getRuntimeName();
		String averageName = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties()
				.get(1).getRuntimeName();

		String windowType = SepaUtils.getOneOfProperty(invocationGraph, "window-type");
		String groupBy = SepaUtils.getMappingPropertyName(invocationGraph, "group-by");

		ObserveNumericalWindowParameters params = new ObserveNumericalWindowParameters(invocationGraph, valueLimit,
				threshold, toObserve, windowType, windowSize, groupBy, messageName, averageName);

		try {
			invokeEPRuntime(params, ObserveNumericalWindow::new, invocationGraph);
			return new Response(invocationGraph.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(invocationGraph.getElementId(), false, e.getMessage());
		}
	}

}
