package de.fzi.cep.sepa.sources.samples.mobile;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.vocabulary.XSD;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class MobileLocationStream implements EventStreamDeclarer {

	@Override
	public EventStream declareModel(SEP sep) {
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "position_x", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "position_y", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "userName", "", de.fzi.cep.sepa.commons.Utils.createURI("http://foaf/name")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri("tcp://localhost");
		grounding.setTopicName("SEPA.SEP.Mobile.Geo");
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Mobile Location Stream");
		stream.setDescription("Provides a stream of the current location of a user");
		stream.setUri(sep.getUri() + "/location");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Location_Icon" +"_HQ.png");
		
		return stream;
	}

	@Override
	public void executeStream() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return false;
	}

}
