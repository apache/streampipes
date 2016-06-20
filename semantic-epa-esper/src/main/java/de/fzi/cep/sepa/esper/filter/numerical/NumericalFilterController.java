package de.fzi.cep.sepa.esper.filter.numerical;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.NumericalOperator;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;

public class NumericalFilterController extends FlatEpDeclarer<NumericalFilterParameter> {

	@Override
	public SepaDescription declareModel() {
			
		
		SepaDescription desc = new SepaDescription("numericalfilter", "Numerical Filter", "Numerical Filter Description");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Numerical_Filter_Icon_HQ.png");
		
		List<EventProperty> propertyRestrictions = new ArrayList<>();
		EventProperty e1 = EpRequirements.numberReq();
		
		propertyRestrictions.add(e1);
		
		EventStream stream = new EventStream();
		EventSchema schema = new EventSchema();
		schema.setEventProperties(propertyRestrictions);
		stream.setEventSchema(schema);
		desc.addEventStream(stream);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new RenameOutputStrategy("Rename", "NumericalFilterResult"));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Filter Operation", "Specifies the filter operation that should be applied on the field");
		operation.addOption(new Option("<"));
		operation.addOption(new Option("<="));
		operation.addOption(new Option(">"));
		operation.addOption(new Option(">="));
		operation.addOption(new Option("=="));
		staticProperties.add(operation);
	
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "number", "Specifies the field name where the filter operation should be applied on.", ""));
		
		staticProperties.add(new FreeTextStaticProperty("value", "Threshold value", "Specifies a threshold value."));
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
		
		NumericalFilterParameter staticParam = new NumericalFilterParameter(sepa, Double.parseDouble(threshold), NumericalOperator.valueOf(operation), filterProperty);
		
		try {
			invokeEPRuntime(staticParam, NumericalFilter::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
