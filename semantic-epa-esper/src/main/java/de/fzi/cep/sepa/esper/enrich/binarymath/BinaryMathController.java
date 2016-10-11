package de.fzi.cep.sepa.esper.enrich.binarymath;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.esper.enrich.math.Operation;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryMathController extends FlatEpDeclarer<BinaryMathParameter>{

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("math-binary", "Math EPA (Binary)",
				"Performs mathematical operations on event properties by taking two different event streams as an input");
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		try {
			EventStream stream1 = makeEventStream();
			EventStream stream2 = makeEventStream();

			desc.addEventStream(stream1);
			desc.addEventStream(stream2);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
			List<EventProperty> appendProperties = new ArrayList<>();
			
			EventProperty result = new EventPropertyPrimitive(XSD._double.toString(),
					"result", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		
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

	private EventStream makeEventStream() {
		EventStream stream = new EventStream();
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<>();
		EventProperty e1 = EpRequirements.numberReq();
		eventProperties.add(e1);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		return stream;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
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

		return submit(staticParam, BinaryMath::new, sepa);
	}
}
