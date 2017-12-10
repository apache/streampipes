package org.streampipes.pe.sources.samples.wunderbar;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;

public class AngularSpeedStream extends AbstractWunderbarStream {
	
	public AngularSpeedStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp(Labels.empty(), "x", "http://schema.org/angularSpeedX"));
		properties.add(EpProperties.doubleEp(Labels.empty(), "y", "http://schema.org/angularSpeedY"));
		properties.add(EpProperties.doubleEp(Labels.empty(), "z", "http://schema.org/angularSpeedZ"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Gyroscope.png");
		stream.setMeasurementCapability(mc("GyroscopeCapability"));
		stream.setMeasurementObject(mo("FestoCebitDevice"));
		return stream;
	}

}
