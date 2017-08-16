package org.streampipes.pe.processors.esper.enrich.math;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MathController extends StandaloneEventProcessorDeclarerSingleton<MathParameter> {

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("math", "Math EPA",
				"performs simple calculations on event properties");
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		desc.setIconUrl(EsperConfig.getIconUrl("math-icon"));
		try {
			EventStream stream1 = new EventStream();

			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = EpRequirements.numberReq();
			EventProperty e2 = EpRequirements.numberReq();
			eventProperties.add(e1);
			eventProperties.add(e2);
			
			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri("http://localhost:8090/" + desc.getElementId());
			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
			List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
			
			EventProperty result = new EventPropertyPrimitive(XSD._long.toString(),
					"delay", "", Utils.createURI("http://schema.org/Number"));
		
			appendProperties.add(result);

			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Select Operation", "");
			operation.addOption(new Option("+"));
			operation.addOption(new Option("-"));
			operation.addOption(new Option("/"));
			operation.addOption(new Option("*"));
			
			staticProperties.add(operation);
			
			// Mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "leftOperand", "Select left operand", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "rightOperand", "Select right operand", ""));
			desc.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<MathParameter, EventProcessor<MathParameter>> onInvocation(SepaInvocation sepa) {
		String operation = SepaUtils.getOneOfProperty(sepa,
						"operation");

		String leftOperand = SepaUtils.getMappingPropertyName(sepa,
						"leftOperand");

		String rightOperand = SepaUtils.getMappingPropertyName(sepa,
						"rightOperand");

		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);

		String appendPropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "delay");

		Operation arithmeticOperation;
		if (operation.equals("+")) arithmeticOperation = Operation.ADD;
		else if (operation.equals("-")) arithmeticOperation = Operation.SUBTRACT;
		else if (operation.equals("*")) arithmeticOperation = Operation.MULTIPLY;
		else arithmeticOperation = Operation.DIVIDE;

		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}

		MathParameter staticParam = new MathParameter(sepa,
						selectProperties,
						arithmeticOperation,
						leftOperand,
						rightOperand,
						appendPropertyName);

		return new ConfiguredEventProcessor<>(staticParam, Math::new);
	}
}
