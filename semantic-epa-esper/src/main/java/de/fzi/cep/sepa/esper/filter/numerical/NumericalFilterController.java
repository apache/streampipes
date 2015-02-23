package de.fzi.cep.sepa.esper.filter.numerical;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.impl.GraphImpl;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;

import de.fzi.cep.sepa.esper.util.NumericalOperator;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.storage.util.Transformer;

public class NumericalFilterController extends EsperDeclarer<NumericalFilterParameter> {

	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventProperty property = new EventPropertyPrimitive("name", "description", "a", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
	
		eventProperties.add(property);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SEPA desc = new SEPA("/sepa/numericalfilter", "Numerical Filter", "Numerical Filter Description", "", "/sepa/numericalfilter", domains);
		
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Numerical_Filter_Icon_HQ.png");
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new RenameOutputStrategy("Rename", "NumericalFilterResult"));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Operation");
		operation.addOption(new Option("<"));
		operation.addOption(new Option("<="));
		operation.addOption(new Option(">"));
		operation.addOption(new Option(">="));
		staticProperties.add(operation);
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(property.getElementName()), "number", "Select mapping property"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticProperties.add(new FreeTextStaticProperty("value", "Threshold value"));
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		
		try {
		logger.info(Utils.asString(Transformer.generateCompleteGraph(new GraphImpl(), sepa)));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		String threshold = ((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "value"))).getValue();
		String stringOperation = SEPAUtils.getOneOfProperty(sepa,
				"operation");
		
		String operation = "GT";
		
		if (stringOperation.equals("<=")) operation = "LT";
		else if (stringOperation.equals("<")) operation="LE";
		else if (stringOperation.equals(">=")) operation = "GE";
		
		
		String filterProperty = SEPAUtils.getMappingPropertyName(sepa,
				"number", true);
		
		logger.info("Text Property: " +filterProperty);
	
		String topicPrefix = "topic://";
		NumericalFilterParameter staticParam = new NumericalFilterParameter(topicPrefix + sepa.getInputStreams().get(0).getEventGrounding().getTopicName(), topicPrefix + sepa.getOutputStream().getEventGrounding().getTopicName(), sepa.getInputStreams().get(0).getEventSchema().toPropertyList(), Collections.<String> emptyList(), Integer.parseInt(threshold), NumericalOperator.valueOf(operation), filterProperty);
		
		try {
			
			return runEngine(staticParam, NumericalFilter::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}

}
