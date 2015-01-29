package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;

import de.fzi.cep.sepa.commonss.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class TwitterGeoStream implements EventStreamDeclarer{

	@Override
	public EventStream declareModel(SEP sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventProperty(XSD._string.toString(), "text", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/text")));
		eventProperties.add(new EventProperty(XSD._long.toString(), "timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "latitude", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "longitude", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventProperty(XSD._string.toString(), "userName", "", de.fzi.cep.sepa.commons.Utils.createURI("http://foaf/name")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri(Configuration.TCP_SERVER_URL);
		grounding.setTopicName("SEPA.SEP.Twitter.Geo");
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Twitter Geo Stream");
		stream.setDescription("Twitter Geo Stream Description");
		stream.setUri(sep.getUri() + "/geo");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Tweet_Icon" +"_HQ.png");
		
		return stream;
	}

	@Override
	public void executeStream() {
		// TODO Auto-generated method stub
		
	}
	
	

	@Override
	public boolean isExecutable() {
		return false;
	}

}
