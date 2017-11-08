package org.streampipes.pe.sources.samples.proveit;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.vocabulary.Geo;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.SO;

public class ProveItLocationStream  implements EventStreamDeclarer {
	    
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		stream.setName("Vehicle Position");
		stream.setDescription("Receives vehicle location data from the ProveIT app");
		stream.setUri(sep.getUri() + "location");
		
		EventGrounding grounding = new EventGrounding();
		JmsTransportProtocol protocol = new JmsTransportProtocol("tcp://kalmar29.fzi.de", 61616, "ProveIT.*.GPS");
		grounding.setTransportProtocol(protocol);
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);
	
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(EpProperties.longEp("timestamp", "http://schema.org/DateTime"));
		properties.add(EpProperties.doubleEp("latitude", Geo.lat));
		properties.add(EpProperties.doubleEp("longitude", Geo.lng));
		properties.add(EpProperties.doubleEp("altitude", Geo.alt));
		properties.add(EpProperties.doubleEp("minAccuracy", SO.Number));
		properties.add(EpProperties.stringEp("deviceId", SO.Text));
		properties.add(EpProperties.doubleEp("accuracy", SO.Number));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
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
