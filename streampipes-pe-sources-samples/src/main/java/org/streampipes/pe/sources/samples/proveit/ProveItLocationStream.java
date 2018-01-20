package org.streampipes.pe.sources.samples.proveit;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.Geo;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.SO;

import java.util.ArrayList;
import java.util.List;

public class ProveItLocationStream  implements DataStreamDeclarer {
	    
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = new SpDataStream();
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
		
		properties.add(EpProperties.longEp(Labels.empty(), "timestamp", "http://schema.org/DateTime"));
		properties.add(EpProperties.doubleEp(Labels.empty(), "latitude", Geo.lat));
		properties.add(EpProperties.doubleEp(Labels.empty(), "longitude", Geo.lng));
		properties.add(EpProperties.doubleEp(Labels.empty(), "altitude", Geo.alt));
		properties.add(EpProperties.doubleEp(Labels.empty(), "minAccuracy", SO.Number));
		properties.add(EpProperties.stringEp(Labels.empty(), "deviceId", SO.Text));
		properties.add(EpProperties.doubleEp(Labels.empty(), "accuracy", SO.Number));
		
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
