package org.streampipes.pe.sinks.standalone.samples.route;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.MessageFormat;

public class RouteController implements SemanticEventConsumerDeclarer{

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("route", "Routes", "Displays routes of moving location-based events", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/route_icon.png");
		sec.setCategory(Arrays.asList(EcType.VISUALIZATION_GEO.name()));
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		EventProperty e3 = EpRequirements.stringReq();
		
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
        String pipelineId = invocationGraph.getCorrespondingPipeline();
        return new Response(pipelineId, true);
    }

    @Override
    public Response detachRuntime(String pipelineId) {
        return new Response(pipelineId, true);
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
