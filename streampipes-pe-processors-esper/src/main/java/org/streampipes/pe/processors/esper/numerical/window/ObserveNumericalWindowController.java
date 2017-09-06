package org.streampipes.pe.processors.esper.numerical.window;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObserveNumericalWindowController extends StandaloneEventProcessorDeclarerSingleton<ObserveNumericalWindowParameters> {

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
				Utils.createURI("http://schema.org/text"));
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
	public ConfiguredEventProcessor<ObserveNumericalWindowParameters, EventProcessor<ObserveNumericalWindowParameters>>
	onInvocation(SepaInvocation invocationGraph) {
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

		return new ConfiguredEventProcessor<>(params, ObserveNumericalWindow::new);
	}

}
