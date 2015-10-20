package de.fzi.cep.sepa.esper.filter.numerical;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.NumericalOperator;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.MeasurementRange;
import de.fzi.cep.sepa.model.impl.quality.Resolution;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class NumericalFilterController extends EpDeclarer<NumericalFilterParameter> {

	@Override
	public SepaDescription declareModel() {
			
		SepaDescription desc = new SepaDescription("sepa/numericalfilter", "Numerical Filter", "Numerical Filter Description");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Numerical_Filter_Icon_HQ.png");
		
		List<EventProperty> propertyRestrictions = new ArrayList<>();
		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://schema.org/Number").build();
		
		List<EventPropertyQualityRequirement> numberQualities = new ArrayList<EventPropertyQualityRequirement>();
		numberQualities.add(new EventPropertyQualityRequirement(new MeasurementRange(-50, 0), null));
		numberQualities.add(new EventPropertyQualityRequirement(new Resolution((float) 0.01), new Resolution(10)));
		e1.setRequiresEventPropertyQualities(numberQualities);
		
		
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
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Operation", "");
		operation.addOption(new Option("<"));
		operation.addOption(new Option("<="));
		operation.addOption(new Option(">"));
		operation.addOption(new Option(">="));
		operation.addOption(new Option("=="));
		staticProperties.add(operation);
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "number", "Provide the event property that should be filtered", ""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticProperties.add(new FreeTextStaticProperty("value", "Threshold value", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		String threshold = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "value"))).getValue();
		String stringOperation = SepaUtils.getOneOfProperty(sepa,
				"operation");
		
		String operation = "GT";
		
		if (stringOperation.equals("<=")) operation = "LT";
		else if (stringOperation.equals("<")) operation="LE";
		else if (stringOperation.equals(">=")) operation = "GE";
		else if (stringOperation.equals("==")) operation = "EQ";
		
		
		String filterProperty = SepaUtils.getMappingPropertyName(sepa,
				"number", true);
		
		logger.info("Text Property: " +filterProperty);
	
		String topicPrefix = "topic://";
		NumericalFilterParameter staticParam = new NumericalFilterParameter(sepa, Integer.parseInt(threshold), NumericalOperator.valueOf(operation), filterProperty);
		
		try {
			invokeEPRuntime(staticParam, NumericalFilter::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
