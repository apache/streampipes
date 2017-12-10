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

public class ColorStream extends AbstractWunderbarStream {
	
	public ColorStream(WunderbarVariables variable) {
		super(variable);
	}
	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = prepareStream(sep);
		
		EventSchema schema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		
		properties.add(timestampProperty());
		properties.add(EpProperties.doubleEp(Labels.empty(), "blue", "http://schema.org/luminosityBlue"));
		properties.add(EpProperties.doubleEp(Labels.empty(), "green", "http://schema.org/luminosityGreen"));
		properties.add(EpProperties.doubleEp(Labels.empty(), "red", "http://schema.org/luminosityRed"));
		
		schema.setEventProperties(properties);
		stream.setEventSchema(schema);
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Color.png");
		stream.setMeasurementCapability(mc("ColorDetectionCapability"));
		stream.setMeasurementObject(mo("FestoCebitDevice"));
		return stream;
	}


}
