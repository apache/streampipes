package de.fzi.cep.sepa.esper.enrich.math;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.rio.RDFHandlerException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class MathController extends EpDeclarer<MathParameter>{

	@Override
	public SepaDescription declareModel() {
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		SepaDescription desc = new SepaDescription("/sepa/math", "Math EPA",
				"performs simple calculations on event properties", "", "/sepa/math", domains);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
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
	public boolean invokeRuntime(SepaInvocation sepa) {
		
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
		
		try {
			return invokeEPRuntime(staticParam, Math::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
