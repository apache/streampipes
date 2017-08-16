package org.streampipes.pe.processors.esper.numerical.value;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.commons.Utils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
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
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.helpers.EpRequirements;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObserveNumericalController extends StandaloneEventProcessorDeclarerSingleton<ObserveNumericalParameters> {

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
		desc.setCategory(Arrays.asList(EpaType.FILTER.name()));
		desc.setIconUrl(EsperConfig.getIconUrl("observe-numerical-icon"));

		desc.addEventStream(stream1);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();

		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(), "message", "",
				Utils.createURI("http://schema.org/text"));
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

		return submit(params, ObserveNumerical::new);

	}

}
