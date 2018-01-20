package org.streampipes.pe.sources.samples.ddm;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.XSD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.streampipes.container.declarer.DataStreamDeclarer;
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
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.pe.sources.samples.util.Utils;

public class SwivelTemperature implements DataStreamDeclarer {

	private String topicName;
	private static final Logger logger = LoggerFactory.getLogger("SwivelTemperature");

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {

		SpDataStream stream = new SpDataStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> valueQualities = new ArrayList<EventPropertyQualityDefinition>();
		valueQualities.add(new MeasurementRange(-21, 1300));
		valueQualities.add(new Resolution((float) 0.1));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "",
				org.streampipes.commons.Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "",
				org.streampipes.commons.Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "",
				org.streampipes.commons.Utils.createURI("http://schema.org/Number"), valueQualities));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(100));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.SwivelOilTemperature.topic()));
		grounding
				.setTransportFormats(org.streampipes.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));

		this.topicName = grounding.getTransportProtocol().getTopicName();

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

	@Override
	public void executeStream() {
		long[] variables = { AkerVariables.SwivelOilTemperature.tagNumber() };
		String cont = Utils.performRequest(variables, topicName, "121213123", "212342134");
		logger.info(cont);
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
}
