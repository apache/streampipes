package org.streampipes.pe.sinks.standalone.samples.maparealist;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.pe.sinks.standalone.samples.util.ActionUtils;
import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.Geo;

public class MapAreaController extends ActionController {

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation sec) {
		String newUrl = createWebsocketUri(sec);
		String inputTopic = extractTopic(sec);
		
		String latitudeNw = SepaUtils.getMappingPropertyName(sec, "latitudeNw");
		String longitudeNw = SepaUtils.getMappingPropertyName(sec, "longitudeNw");
		String latitudeSe = SepaUtils.getMappingPropertyName(sec, "latitudeSe");
		String longitudeSe = SepaUtils.getMappingPropertyName(sec, "longitudeSe");
		String labelName = SepaUtils.getMappingPropertyName(sec, "label");
		
		System.out.println(latitudeNw);
		MapAreaParameters mapsParameters = new MapAreaParameters(inputTopic, newUrl, latitudeNw, longitudeNw, latitudeSe, longitudeSe, labelName);
		
		return new MapAreaGenerator(mapsParameters).generateHtml();
	}

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("maparealist", "Map area view (list input)", "", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/Map_Icon_HQ.png");
		sec.setCategory(Arrays.asList(EcType.VISUALIZATION_GEO.name()));
		EventPropertyList listProperty = new EventPropertyList();
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		EventProperty e3 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e4 = EpRequirements.domainPropertyReq(Geo.lng);
		EventProperty e5 = new EventPropertyPrimitive();
		
		eventProperties.add(e1);
		eventProperties.add(e2);
		eventProperties.add(e3);
		eventProperties.add(e4);
		eventProperties.add(e5);
		
		listProperty.setEventProperties(eventProperties);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(Arrays.asList(listProperty));
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "latitudeNw", "Select latitude property (NW)", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "longitudeNw", "Select longitude property (NW)", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e3.getElementName()), "latitudeSe", "Select latitude property (SE)", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e4.getElementName()), "longitudeSe", "Select longitude property (SE)", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e5.getElementName()), "label", "Select Label", ""));

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		sec.setSupportedGrounding(ActionUtils.getSupportedGrounding());
		
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

}
