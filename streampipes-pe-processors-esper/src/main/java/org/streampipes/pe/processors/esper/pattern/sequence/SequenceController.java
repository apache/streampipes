package org.streampipes.pe.processors.esper.pattern.sequence;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MatchingStaticProperty;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequenceController extends StandaloneEventProcessorDeclarerSingleton<SequenceParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		SpDataStream stream1 = new SpDataStream();
		SpDataStream stream2 = new SpDataStream();
		
		EventProperty e1 = new EventPropertyPrimitive();
		EventProperty e2 = new EventPropertyPrimitive();
		
		DataProcessorDescription desc = new DataProcessorDescription("sequence", "Sequence", "Detects a sequence of events in the following form: Event A followed by Event B within X seconds. In addition, both streams can be matched by a common property value (e.g., a.machineId = b.machineId).");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Sequence_Icon_HQ.png");
		desc.setCategory(Arrays.asList(DataProcessorType.PATTERN_DETECT.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(new EventSchema(Arrays.asList(e1)));
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setEventSchema(new EventSchema(Arrays.asList(e2)));
		
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);	
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy(false));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty timeWindowUnit = new OneOfStaticProperty("time-unit", "Time Unit", "Specifies a unit for the time window of the sequence. ");
		timeWindowUnit.addOption(new Option("sec"));
		timeWindowUnit.addOption(new Option("min"));
		timeWindowUnit.addOption(new Option("hrs"));
		staticProperties.add(timeWindowUnit);
		
//		OneOfStaticProperty matchingOperator = new OneOfStaticProperty("matching-operator", "Time Unit", "Specifies a unit for the time window of the sequence. ");
//		matchingOperator.addOption(new Option("=="));
//		matchingOperator.addOption(new Option(">="));
//		matchingOperator.addOption(new Option("<="));
//		matchingOperator.addOption(new Option("<"));
//		matchingOperator.addOption(new Option(">"));
//		matchingOperator.setValueRequired(false);
//		staticProperties.add(matchingOperator);
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("duration", "Time Value", "Specifies the size of the time window.");
		staticProperties.add(duration);
		
		MatchingStaticProperty matchingProperty = new MatchingStaticProperty("matching", "Matching", "Specifies an additional value restriction on both streams.", URI.create(e1.getElementId()), URI.create(e2.getElementId()));
		//staticProperties.add(matchingProperty);
		
//		MappingProperty m1 = new MappingPropertyUnary(URI.create(e1.getElementId()), "partition", "Partition", "The streams will be partitioned based on the selected property.");
//		m1.setValueRequired(false);
//		staticProperties.add(m1);
		
		//staticProperties.add(new MatchingStaticProperty("select matching", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<SequenceParameters> onInvocation(DataProcessorInvocation
																																																								 invocationGraph) {
		String timeUnit = SepaUtils.getOneOfProperty(invocationGraph, "time-unit");
		//String matchingOperator = SepaUtils.getOneOfProperty(invocationGraph, "matching-operator");
		String matchingOperator = "";
		int duration = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(invocationGraph, "duration"));
		//String partitionProperty = SepaUtils.getMappingPropertyName(invocationGraph, "partition", true);
		//List<String> matchingProperties = SepaUtils.getMatchingPropertyNames(invocationGraph, "matching");
		List<String> matchingProperties = new ArrayList<>();
		SequenceParameters params = new SequenceParameters(invocationGraph, timeUnit, matchingOperator, duration, matchingProperties);

		return new ConfiguredEventProcessor<>(params, Sequence::new);
	}

}
