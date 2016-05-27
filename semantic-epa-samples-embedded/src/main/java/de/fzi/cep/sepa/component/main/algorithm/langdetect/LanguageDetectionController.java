package de.fzi.cep.sepa.component.main.algorithm.langdetect;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.component.config.Config;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class LanguageDetectionController extends FlatEpDeclarer<LanguageDetectionParameters>{

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventPropertyPrimitive e1 = new EventPropertyPrimitive(Utils.createURI(SO.Text));
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("langdetect", "Language Detection", "Detects the language of a textual property");
		desc.setIconUrl(Config.iconBaseUrl + "/Language_Detection_Icon_HQ.png");
		//TODO check if needed
		stream1.setUri(Config.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(),
				"language", "", de.fzi.cep.sepa.commons.Utils.createURI(SO.Text));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		MappingProperty textMapping = new MappingPropertyUnary(URI.create(e1.getElementName()), "text", "text", "text property: ");
		staticProperties.add(textMapping);
	
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {

		String textMapping = SepaUtils.getMappingPropertyName(sepa,
				"text");
				
		LanguageDetectionParameters staticParam = new LanguageDetectionParameters(sepa, textMapping);
		
		try {
			invokeEPRuntime(staticParam, LanguageDetection::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
