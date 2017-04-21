package de.fzi.cep.sepa.esper.pattern.increase;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.PropertyValueSpecification;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.sdk.StaticProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IncreaseController extends FlatEpDeclarer<IncreaseParameters> {

	@Override
	public SepaDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventSchema schema = new EventSchema();
		EventProperty e1 = EpRequirements.numberReq();
		schema.setEventProperties(Arrays.asList(e1));
		
		SepaDescription desc = new SepaDescription("increase", "Increase", "Detects the increase of a numerical field over a customizable time window. Example: A temperature value increases by 10 percent within 5 minutes.");
		desc.setCategory(Arrays.asList(EpaType.PATTERN_DETECT.name()));
		desc.setIconUrl(EsperConfig.getIconUrl("increase-icon"));
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(new EventSchema(Arrays.asList(e1)));
	
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy(true));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Increase/Decrease", "Specifies the type of operation the processor should perform.");
		operation.addOption(new Option("Increase"));
		operation.addOption(new Option("Decrease"));
		
		staticProperties.add(operation);
		staticProperties.add(StaticProperties.integerFreeTextProperty("increase", "Percentage of Increase/Decrease", "Specifies the increase in percent (e.g., 100 indicates an increase by 100 percent within the specified time window.", new PropertyValueSpecification(0, 500, 1)));
		
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("duration", "Time Window Length (Seconds)", "Specifies the size of the time window in seconds.");
		staticProperties.add(duration);
		
		MappingPropertyUnary mapping = new MappingPropertyUnary(URI.create(e1.getElementId()), "mapping", "Value to observe", "Specifies the value that should be monitored.");
		staticProperties.add(mapping);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		String operation = SepaUtils.getOneOfProperty(invocationGraph, "operation");
		System.out.println(operation);
		int increase = (int) Double.parseDouble(SepaUtils.getFreeTextStaticPropertyValue(invocationGraph, "increase"));
		int duration = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(invocationGraph, "duration"));
		String mapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping");
		IncreaseParameters params = new IncreaseParameters(invocationGraph, getOperation(operation), increase, duration, mapping);

		return submit(params, Increase::new, invocationGraph);

	}
	
	private Operation getOperation(String operation) {
		if (operation.equals("Increase")) return Operation.INCREASE;
		else return Operation.DECREASE;
	}
	
}
