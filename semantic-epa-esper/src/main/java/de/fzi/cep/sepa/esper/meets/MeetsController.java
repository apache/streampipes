package de.fzi.cep.sepa.esper.meets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.pattern.PatternParameters;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class MeetsController extends EpDeclarer<PatternParameters>{

	@Override
	public boolean invokeRuntime(SepaInvocation sepa) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SepaDescription declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		// 1st location-based stream
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
				"http://test.de/latitude"));
		EventProperty e2 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
				"http://test.de/longitude"));
		eventProperties.add(e1);
		eventProperties.add(e2);
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		
		EventStream stream2 = new EventStream();
		EventSchema schema2 = new EventSchema();
		List<EventProperty> eventProperties2 = new ArrayList<EventProperty>();
		EventProperty e3 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
				"http://test.de/latitude"));
		EventProperty e4 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
				"http://test.de/longitude"));
		eventProperties2.add(e3);
		eventProperties2.add(e4);
		schema2.setEventProperties(eventProperties2);
		stream2.setEventSchema(schema2);
		
		SepaDescription desc = new SepaDescription("/sepa/meets", "Geospatial distance", "Detects two location-based streams within a given distance", "", "/sepa/meets", domains);
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
		
		
		OneOfStaticProperty timeWindowUnit = new OneOfStaticProperty("time unit", "select time unit");
		timeWindowUnit.addOption(new Option("sec"));
		timeWindowUnit.addOption(new Option("min"));
		timeWindowUnit.addOption(new Option("hrs"));
		staticProperties.add(timeWindowUnit);
		
		FreeTextStaticProperty timeWindow = new FreeTextStaticProperty("Time Window", "Select time window");
		staticProperties.add(timeWindow);
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("Distance", "Select minimum distance");
		staticProperties.add(duration);
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "Latitude", "Select Latitude Mapping (Stream 1)"));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "Longitude", "Select Longitude Mapping (Stream 1)"));
			staticProperties.add(new MappingPropertyUnary(new URI(e3.getElementName()), "Latitude", "Select Latitude Mapping (Stream 2)"));
			staticProperties.add(new MappingPropertyUnary(new URI(e4.getElementName()), "Longitude", "Select Longitude Mapping (Stream 2)"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

}
