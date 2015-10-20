package de.fzi.cep.sepa.actions.samples.route;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;

public class RouteController implements SemanticEventConsumerDeclarer{

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("route", "Routes", "Displays routes of moving location-based events", "");
		//sec.setIconUrl(ActionConfig.iconBaseUrl + "/Map_Icon_HQ.png");
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(Utils.createURI("http://test.de/latitude"));
		EventProperty e2 = new EventPropertyPrimitive(Utils.createURI("http://test.de/longitude"));
		EventProperty e3 = new EventPropertyPrimitive(Utils.createURI(SO.Text));
		
		eventProperties.add(e1);
		eventProperties.add(e2);
		eventProperties.add(e3);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "latitude", "Select latitude property", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "longitude", "Select longitude property", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e3.getElementName()), "label", "Select Label", ""));

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		grounding.setTransportProtocol(new JmsTransportProtocol());
		sec.setSupportedGrounding(grounding);
		
		return sec;
	}

	@Override
	public Response invokeRuntime(SecInvocation invocationGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response detachRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		String newUrl = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname().replace("tcp",  "ws") + ":61614";
		
		String labelName = SepaUtils.getMappingPropertyName(graph, "label");
		String latitudeName = SepaUtils.getMappingPropertyName(graph, "latitude");
		String longitudeName = SepaUtils.getMappingPropertyName(graph, "longitude");
		
		RouteParameters routeParams = new RouteParameters("/topic/" + graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), newUrl, latitudeName, longitudeName, labelName);
		
		return new RouteGenerator(routeParams).generateHtml();
	}

}
