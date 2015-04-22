package de.fzi.cep.sepa.actions.samples.maps;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph;
import de.fzi.cep.sepa.model.util.SEPAUtils;


public class MapsController extends ActionController {

	@Override
	public SEC declareModel() {
		
		SEC sec = new SEC("/maps", "Map view", "Displays location-based events on a map", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/Map_Icon_HQ.png");
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(Utils.createURI("http://test.de/latitude"));
		EventProperty e2 = new EventPropertyPrimitive(Utils.createURI("http://test.de/longitude"));
		EventProperty e3 = new EventPropertyPrimitive(Utils.createURI("http://test.de/text"));
		
		eventProperties.add(e1);
		eventProperties.add(e2);
		eventProperties.add(e3);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "latitude", "Select latitude property"));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "longitude", "Select longitude property"));
		staticProperties.add(new MappingPropertyUnary(URI.create(e3.getElementName()), "label", "Select Label"));

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		
		return sec;
	}

	@Override
	public String invokeRuntime(SECInvocationGraph sec) {
		
		String newUrl = createWebsocketUri(sec);
		String inputTopic = extractTopic(sec);
		
		String latitudeName = SEPAUtils.getMappingPropertyName(sec, "latitude");
		String longitudeName = SEPAUtils.getMappingPropertyName(sec, "longitude");
		String labelName = SEPAUtils.getMappingPropertyName(sec, "label");
		
		MapsParameters mapsParameters = new MapsParameters(inputTopic, newUrl, latitudeName, longitudeName, labelName);
		
		return new MapsGenerator(mapsParameters).generateHtml();
	}

	@Override
	public boolean detachRuntime(SECInvocationGraph sec) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
