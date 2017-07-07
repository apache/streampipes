package org.streampipes.pe.processors.standalone.languagedetection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.streampipes.pe.processors.standalone.config.Config;
import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.wrapper.standalone.declarer.FlatEpDeclarer;
import org.streampipes.container.util.StandardTransportFormat;

public class LanguageDetectionController extends FlatEpDeclarer<LanguageDetectionParameters>{

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventPropertyPrimitive e1 = EpRequirements.stringReq();
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("sepa/langdetect", "Language Detection", "Detects the language of a textual property");
		desc.setIconUrl(Config.iconBaseUrl + "/Language_Detection_Icon_HQ.png");
		//TODO check if needed
		stream1.setUri(Config.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(),
				"language", "", Utils.createURI(SO.Text));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		MappingProperty textMapping = new MappingPropertyUnary(URI.create(e1.getElementName()), "text", "text property: ", "");
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
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), true);
		}
		
	}

}
