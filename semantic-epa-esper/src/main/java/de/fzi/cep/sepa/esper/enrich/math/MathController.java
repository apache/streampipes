package de.fzi.cep.sepa.esper.enrich.math;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFHandlerException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.storage.util.Transformer;

public class MathController extends EsperDeclarer<MathParameter>{

	@Override
	public SEPA declareModel() {
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		SEPA desc = new SEPA("/sepa/math", "Math EPA",
				"performs simple calculations on event properties", "", "/sepa/math", domains);
		try {
			EventStream stream1 = new EventStream();

			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
					"http://schema.org/Number"));
			EventProperty e2 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
					"http://schema.org/Number"));
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
					"delay", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		
			appendProperties.add(result);

			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Select Operation");
			operation.addOption(new Option("+"));
			operation.addOption(new Option("-"));
			operation.addOption(new Option("/"));
			operation.addOption(new Option("*"));
			
			staticProperties.add(operation);
			
			// Mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "leftOperand", "Select left operand"));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "rightOperand", "Select right operand"));
			desc.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		
		String topicPrefix = "topic://";
		String inputTopic = topicPrefix + sepa.getInputStreams().get(0).getEventGrounding().getTopicName();
		String outputTopic = topicPrefix + sepa.getOutputStream().getEventGrounding().getTopicName();
		
		String operation = SEPAUtils.getOneOfProperty(sepa,
				"operation");
		
		String leftOperand = SEPAUtils.getMappingPropertyName(sepa,
				"leftOperand");
		
		String rightOperand = SEPAUtils.getMappingPropertyName(sepa,
				"rightOperand");
		
		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);
		
		String appendPropertyName = SEPAUtils.getEventPropertyName(strategy.getEventProperties(), "delay");
	
		Operation arithmeticOperation;
		if (operation.equals("+")) arithmeticOperation = Operation.ADD;
		else if (operation.equals("-")) arithmeticOperation = Operation.SUBTRACT;
		else if (operation.equals("*")) arithmeticOperation = Operation.MULTIPLY;
		else arithmeticOperation = Operation.DIVIDE;
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getPropertyName());
		}
		
		MathParameter staticParam = new MathParameter(inputTopic, 
				outputTopic, 
				sepa.getInputStreams().get(0).getEventSchema().toPropertyList(), 
				sepa.getInputStreams().get(0).getEventSchema().toPropertyList(),
				selectProperties, 
				arithmeticOperation, 
				leftOperand, 
				rightOperand,
				appendPropertyName);	
		
		try {
			return runEngine(staticParam, Math::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		super.runtime.discard();
		return false;
	}
	
	

}
