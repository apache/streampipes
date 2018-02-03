package org.streampipes.pe.processors.esper.meets;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.vocabulary.Geo;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MeetsController extends StandaloneEventProcessorDeclarerSingleton<MeetsParameters> {

	@Override
	public ConfiguredEventProcessor<MeetsParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
		return null;
	}

	@Override
	public DataProcessorDescription declareModel() {
		
		// 1st location-based stream
		SpDataStream stream1 = new SpDataStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		eventProperties.add(e1);
		eventProperties.add(e2);
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		
		SpDataStream stream2 = new SpDataStream();
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
		
		DataProcessorDescription desc = new DataProcessorDescription("meets", "Geospatial distance", "Detects two location-based streams within a given distance");
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
