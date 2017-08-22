package org.streampipes.pe.sources.samples.wunderbar;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class AccelerometerStream extends AbstractWunderbarStream {
	
	public AccelerometerStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp("x", "http://schema.org/accelerometerX"));
		properties.add(EpProperties.doubleEp("y", "http://schema.org/accelerometerY"));
		properties.add(EpProperties.doubleEp("z", "http://schema.org/accelerometerZ"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Acceleration1.png");
		stream.setMeasurementCapability(mc("AccelerationCapability"));
		stream.setMeasurementObject(mo("FestoCebitDevice"));
		return stream;
	}

}
