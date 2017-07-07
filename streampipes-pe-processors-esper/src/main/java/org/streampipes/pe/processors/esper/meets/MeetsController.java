package org.streampipes.pe.processors.esper.meets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.CustomOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.runtime.flat.declarer.FlatEpDeclarer;
import org.streampipes.container.util.StandardTransportFormat;

public class MeetsController extends FlatEpDeclarer<MeetsParameters>{

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SepaDescription declareModel() {
		
		// 1st location-based stream
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		eventProperties.add(e1);
		eventProperties.add(e2);
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		
		EventStream stream2 = new EventStream();
		EventSchema schema2 = new EventSchema();
		List<EventProperty> eventProperties2 = new ArrayList<EventProperty>();
		EventProperty e3 = new EventPropertyPrimitive(Utils.createURI(
				"http://test.de/latitude"));
		EventProperty e4 = new EventPropertyPrimitive(Utils.createURI(
				"http://test.de/longitude"));
		eventProperties2.add(e3);
		eventProperties2.add(e4);
		schema2.setEventProperties(eventProperties2);
		stream2.setEventSchema(schema2);
		
		SepaDescription desc = new SepaDescription("meets", "Geospatial distance", "Detects two location-based streams within a given distance");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/And_Icon_HQ.png");
		
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);
		
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy());
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		
		OneOfStaticProperty timeWindowUnit = new OneOfStaticProperty("time unit", "select time unit", "");
		timeWindowUnit.addOption(new Option("sec"));
		timeWindowUnit.addOption(new Option("min"));
		timeWindowUnit.addOption(new Option("hrs"));
		staticProperties.add(timeWindowUnit);
		
		FreeTextStaticProperty timeWindow = new FreeTextStaticProperty("Time Window", "Select time window", "");
		staticProperties.add(timeWindow);
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("Distance", "Select minimum distance", "");
		staticProperties.add(duration);
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "Latitude", "Select Latitude Mapping (Stream 1)", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "Longitude", "Select Longitude Mapping (Stream 1)", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e3.getElementName()), "Latitude", "Select Latitude Mapping (Stream 2)", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e4.getElementName()), "Longitude", "Select Longitude Mapping (Stream 2)", ""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

}
