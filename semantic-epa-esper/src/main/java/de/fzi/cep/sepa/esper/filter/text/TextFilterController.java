package de.fzi.cep.sepa.esper.filter.text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StringOperator;
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
import de.fzi.cep.sepa.util.StandardTransportFormat;


public class TextFilterController extends FlatEpDeclarer<TextFilterParameter> {
	
	@Override
	public SepaDescription declareModel() {
			
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventProperty property = EpRequirements.stringReq();
		
		eventProperties.add(property);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("sepa/textfilter", "Text Filter", "Text Filter Description");
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Textual_Filter_Icon_HQ.png");
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +"/" +desc.getElementId());
		desc.addEventStream(stream1);
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new RenameOutputStrategy("Enrich", "EnrichedMovementAnalysis"));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Select Operation", "");
		operation.addOption(new Option("MATCHES"));
		operation.addOption(new Option("CONTAINS"));
		staticProperties.add(operation);
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(property.getElementName()), "text", "Select Text Property", ""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticProperties.add(new FreeTextStaticProperty("keyword", "Select Keyword", ""));
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
			
		String keyword = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "keyword"))).getValue();
		String operation = SepaUtils.getOneOfProperty(sepa,
				"operation");
		String filterProperty = SepaUtils.getMappingPropertyName(sepa,
				"text");
		
		logger.info("Text Property: " +filterProperty);
	
		TextFilterParameter staticParam = new TextFilterParameter(sepa, 
				keyword, 
				StringOperator.valueOf(operation), 
				filterProperty);
		
		
		try {
			invokeEPRuntime(staticParam, TextFilter::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
