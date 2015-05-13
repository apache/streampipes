package de.fzi.cep.sepa.esper.filter.text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;


public class TextFilterController extends EsperDeclarer<TextFilterParameter> {
	
	@Override
	public SEPA declareModel() {
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventProperty property = new EventPropertyPrimitive("name", "description", "a", de.fzi.cep.sepa.commons.Utils.createURI(SO.Text));
	
		eventProperties.add(property);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SEPA desc = new SEPA("/sepa/textfilter", "Text Filter", "Text Filter Description", "", "/sepa/textfilter", domains);
		
		EventGrounding supportedGrounding = new EventGrounding();
		supportedGrounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Thrift)));
		
		desc.setSupportedGrounding(supportedGrounding);
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Textual_Filter_Icon_HQ.png");
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +"/" +desc.getElementId());
		desc.addEventStream(stream1);
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new RenameOutputStrategy("Enrich", "EnrichedMovementAnalysis"));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Select Operation");
		operation.addOption(new Option("MATCHES"));
		operation.addOption(new Option("CONTAINS"));
		staticProperties.add(operation);
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(property.getElementName()), "text", "Select Text Property"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticProperties.add(new FreeTextStaticProperty("keyword", "Select Keyword"));
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
			
			String keyword = ((FreeTextStaticProperty) (SEPAUtils
					.getStaticPropertyByName(sepa, "keyword"))).getValue();
			String operation = SEPAUtils.getOneOfProperty(sepa,
					"operation");
			String filterProperty = SEPAUtils.getMappingPropertyName(sepa,
					"text");
			
			logger.info("Text Property: " +filterProperty);
		
			TextFilterParameter staticParam = new TextFilterParameter(sepa, 
					keyword, 
					StringOperator.valueOf(operation), 
					filterProperty);
			
			
			try {
				
				return invokeEPRuntime(staticParam, TextFilter::new, sepa);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
	}
}
