package org.streampipes.pe.processors.esper.numerical.value;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.vocabulary.XSD;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObserveNumericalController extends StandaloneEventProcessorDeclarerSingleton<ObserveNumericalParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventPropertyPrimitive e1 = EpRequirements.numberReq();
		eventProperties.add(e1);

		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);

		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);

		DataProcessorDescription desc = new DataProcessorDescription("observenumerical", "Observe Numerical",
				"Throws an alert when value exceeds a threshold value");
		desc.setCategory(Arrays.asList(DataProcessorType.FILTER.name()));
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
	public ConfiguredEventProcessor<ObserveNumericalParameters>
	onInvocation(DataProcessorInvocation invocationGraph, ProcessingElementParameterExtractor extractor) {
		String valueLimit = SepaUtils.getOneOfProperty(invocationGraph, "value-limit");

		String outputProperty = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties().get(0).getRuntimeName();

		double threshold = Double.parseDouble(
						((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "threshold")))
										.getValue());

		String value = SepaUtils.getMappingPropertyName(invocationGraph, "number");

		ObserveNumericalParameters params = new ObserveNumericalParameters(invocationGraph, valueLimit, threshold, value, outputProperty);

		return new ConfiguredEventProcessor<>(params, ObserveNumerical::new);
	}

}
