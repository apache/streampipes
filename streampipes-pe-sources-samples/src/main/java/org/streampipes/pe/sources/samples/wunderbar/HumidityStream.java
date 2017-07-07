package org.streampipes.pe.sources.samples.wunderbar;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class HumidityStream extends AbstractWunderbarStream {
	
	public HumidityStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp("humidity", "http://schema.org/humidity"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Humidity.png");
		stream.setMeasurementCapability(mc("HumidityMeasurementCapability"));
		stream.setMeasurementObject(mo("CebitHall6"));
		return stream;
	}


}
