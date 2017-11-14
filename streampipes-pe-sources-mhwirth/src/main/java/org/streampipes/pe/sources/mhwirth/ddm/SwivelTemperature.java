package org.streampipes.pe.sources.mhwirth.ddm;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.quality.Frequency;
import org.streampipes.model.quality.MeasurementRange;
import org.streampipes.model.quality.Resolution;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;
import org.streampipes.pe.sources.mhwirth.config.ProaSenseSettings;
import org.streampipes.pe.sources.mhwirth.config.SourcesConfig;
import org.streampipes.commons.Utils;

public class SwivelTemperature extends AbstractAlreadyExistingStream {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {

		SpDataStream stream = new SpDataStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> valueQualities = new ArrayList<EventPropertyQualityDefinition>();
		valueQualities.add(new MeasurementRange(-21, 1300));
		valueQualities.add(new Resolution((float) 0.1));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "",
				Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "",
				Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "",
				Utils.createURI(SO.Number), valueQualities));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(100));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.SwivelOilTemperature.topic()));
		grounding
				.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);
		stream.setName(AkerVariables.SwivelOilTemperature.eventName());
		stream.setDescription(AkerVariables.SwivelOilTemperature.description());
		stream.setUri(sep.getUri() + "/swivelTemperature");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Temperature_Icon" + "_HQ.png");

		return stream;
	}

}
