package de.fzi.cep.sepa.esper.filter.numerical;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.NumericalOperator;
import de.fzi.cep.sepa.esper.util.StandardTransportFormat;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
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
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class NumericalFilterController extends EsperDeclarer<NumericalFilterParameter> {

	@Override
	public SepaDescription declareModel() {
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		SepaDescription desc = new SepaDescription("/sepa/numericalfilter", "Numerical Filter", "Numerical Filter Description", "", "/sepa/numericalfilter", domains);
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Numerical_Filter_Icon_HQ.png");
		
		List<EventProperty> propertyRestrictions = new ArrayList<>();
		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://schema.org/Number").build();
		propertyRestrictions.add(e1);
		
		
		//EventSchema schema1 = new EventSchema();
		//schema1.setEventProperties(propertyRestrictions);
		
		EventStream stream1 = StreamBuilder
				.createStreamRestriction(EsperConfig.serverUrl + "/" +desc.getElementId())
				.schema(
						SchemaBuilder.create()
							.properties(propertyRestrictions)
							.build()
						).build();
		
		
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
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "number", "Provide the event property that should be filtered"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticProperties.add(new FreeTextStaticProperty("value", "Threshold value"));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SepaInvocation sepa) {
		
		String threshold = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(sepa, "value"))).getValue();
		String stringOperation = SepaUtils.getOneOfProperty(sepa,
				"operation");
		
		String operation = "GT";
		
		if (stringOperation.equals("<=")) operation = "LT";
		else if (stringOperation.equals("<")) operation="LE";
		else if (stringOperation.equals(">=")) operation = "GE";
		
		
		String filterProperty = SepaUtils.getMappingPropertyName(sepa,
				"number", true);
		
		logger.info("Text Property: " +filterProperty);
	
		String topicPrefix = "topic://";
		NumericalFilterParameter staticParam = new NumericalFilterParameter(sepa, Integer.parseInt(threshold), NumericalOperator.valueOf(operation), filterProperty);
		
		try {
			
			return invokeEPRuntime(staticParam, NumericalFilter::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
