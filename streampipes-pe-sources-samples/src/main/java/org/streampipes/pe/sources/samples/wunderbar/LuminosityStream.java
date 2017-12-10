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

public class LuminosityStream extends AbstractWunderbarStream {
	
	public LuminosityStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp(Labels.empty(), "luminosity", "http://schema.org/luminosity"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Brightness.png");
		stream.setMeasurementCapability(mc("BrightnessMeasurementCapability"));
		stream.setMeasurementObject(mo("CebitHall6"));
		return stream;
	}


}
