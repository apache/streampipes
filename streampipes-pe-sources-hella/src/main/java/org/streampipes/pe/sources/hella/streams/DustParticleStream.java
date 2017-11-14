package org.streampipes.pe.sources.hella.streams;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.config.SourcesConfig;

public class DustParticleStream extends EnvironmentalDataStream {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = prepareStream(HellaVariables.Dust.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.addAll(getPreparedProperties());
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin0", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin1", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin2", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin3", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin4", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin5", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin6", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin7", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin8", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin9", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin10", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin11", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin12", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin13", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin14", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "bin15", "", Utils.createURI(SO.Number)));
			
	
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.Dust.eventName());
		stream.setDescription(HellaVariables.Dust.description());
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/icon-dust" +".png");
		stream.setUri(sep.getUri() + "/dust");
		
		return stream;
	}

	@Override
	public boolean isExecutable() {
		return false;
	}

}
