package org.streampipes.pe.sources.samples.wunderbar;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class ColorStream extends AbstractWunderbarStream {
	
	public ColorStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp("blue", "http://schema.org/luminosityBlue"));
		properties.add(EpProperties.doubleEp("green", "http://schema.org/luminosityGreen"));
		properties.add(EpProperties.doubleEp("red", "http://schema.org/luminosityRed"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Color.png");
		stream.setMeasurementCapability(mc("ColorDetectionCapability"));
		stream.setMeasurementObject(mo("FestoCebitDevice"));
		return stream;
	}


}
