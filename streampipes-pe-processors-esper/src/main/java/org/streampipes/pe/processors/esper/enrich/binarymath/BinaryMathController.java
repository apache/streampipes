package org.streampipes.pe.processors.esper.enrich.binarymath;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.enrich.math.Operation;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.vocabulary.XSD;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryMathController extends StandaloneEventProcessorDeclarerSingleton<BinaryMathParameter> {

	@Override
	public DataProcessorDescription declareModel() {
		
		DataProcessorDescription desc = new DataProcessorDescription("math-binary", "Math EPA (Binary)",
				"Performs mathematical operations on event properties by taking two different event streams as an input");
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		try {
			SpDataStream stream1 = makeEventStream();
			SpDataStream stream2 = makeEventStream();

			desc.addEventStream(stream1);
			desc.addEventStream(stream2);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
			List<EventProperty> appendProperties = new ArrayList<>();
			
			EventProperty result = new EventPropertyPrimitive(XSD._double.toString(),
					"result", "", Utils.createURI("http://schema.org/Number"));
		
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
			staticProperties.add(new MappingPropertyUnary(new URI(stream1.getEventSchema().getEventProperties().get(0).getElementName()), "leftOperand", "Select left operand", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(stream2.getEventSchema().getEventProperties().get(0).getElementName()), "rightOperand", "Select right operand", ""));
			desc.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return desc;
	}

	private SpDataStream makeEventStream() {
		SpDataStream stream = new SpDataStream();
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<>();
		EventProperty e1 = EpRequirements.numberReq();
		eventProperties.add(e1);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		return stream;
	}


	@Override
	public ConfiguredEventProcessor<BinaryMathParameter> onInvocation
					(DataProcessorInvocation sepa) {
		String operation = SepaUtils.getOneOfProperty(sepa,
						"operation");

		String leftOperand = SepaUtils.getMappingPropertyName(sepa,
						"leftOperand");

		String rightOperand = SepaUtils.getMappingPropertyName(sepa,
						"rightOperand");

		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);

		String appendPropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "result");

		Operation arithmeticOperation;
		if (operation.equals("+")) arithmeticOperation = Operation.ADD;
		else if (operation.equals("-")) arithmeticOperation = Operation.SUBTRACT;
		else if (operation.equals("*")) arithmeticOperation = Operation.MULTIPLY;
		else arithmeticOperation = Operation.DIVIDE;

		List<String> selectProperties = sepa
						.getInputStreams()
						.get(0)
						.getEventSchema()
						.getEventProperties()
						.stream()
						.map(EventProperty::getRuntimeName).collect(Collectors.toList());

		BinaryMathParameter staticParam = new BinaryMathParameter(sepa,
						selectProperties,
						arithmeticOperation,
						leftOperand,
						rightOperand,
						appendPropertyName);

		return new ConfiguredEventProcessor<>(staticParam, BinaryMath::new);
	}
}
