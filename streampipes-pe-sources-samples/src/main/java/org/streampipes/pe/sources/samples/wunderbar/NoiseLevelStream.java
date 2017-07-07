package org.streampipes.pe.sources.samples.wunderbar;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class NoiseLevelStream extends AbstractWunderbarStream {
	
	public NoiseLevelStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp("noiseLevel", "http://schema.org/noiseLevel"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/SoundLevel.png");
		stream.setMeasurementCapability(mc("NoiseMeasurementCapability"));
		stream.setMeasurementObject(mo("CebitHall6"));
		return stream;
	}


}
