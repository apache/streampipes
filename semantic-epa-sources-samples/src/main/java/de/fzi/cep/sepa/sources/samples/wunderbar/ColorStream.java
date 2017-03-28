package de.fzi.cep.sepa.sources.samples.wunderbar;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

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
