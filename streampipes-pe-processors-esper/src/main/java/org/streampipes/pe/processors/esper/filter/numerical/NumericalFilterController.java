package org.streampipes.pe.processors.esper.filter.numerical;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.esper.config.EsperConfig;
import org.streampipes.pe.processors.esper.util.NumericalOperator;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.output.RenameOutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.wrapper.standalone.declarer.FlatEventProcessorDeclarer;

public class NumericalFilterController extends FlatEventProcessorDeclarer<NumericalFilterParameter> {

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

		return submit(staticParam, NumericalFilter::new, sepa);

	}
}
