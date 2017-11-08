package org.streampipes.pe.sources.samples.mobile;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.vocabulary.XSD;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.commons.Utils;

public class MobileLocationStream implements EventStreamDeclarer {

	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "position_x", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "position_y", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "userName", "", Utils.createURI("http://foaf/name")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Mobile.Geo"));
		
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
