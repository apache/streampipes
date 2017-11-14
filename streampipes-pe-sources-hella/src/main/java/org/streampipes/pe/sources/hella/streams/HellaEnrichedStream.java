package org.streampipes.pe.sources.hella.streams;

import org.streampipes.commons.Utils;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.util.ArrayList;
import java.util.List;

public class HellaEnrichedStream extends AbstractHellaStream {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = prepareStream(HellaVariables.EnrichedEvent.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "time", "", Utils.createURI(SO.Text)));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW1_SW2", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW2_SW3", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW3_SW8", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW3_SW4", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW4_SW5", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW5_SW11", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW11_SW8", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW8_SW9", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW9_SW1", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW8_PM1", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW9_PM2", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "PM1_SW1", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "PM2_SW1", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW1_IMM1", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW2_IMM2", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW3_IMM3", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW4_IMM4", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "SW5_IMM5", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "IMM1_SW2", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "IMM2_SW3", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "IMM3_SW4", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "IMM4_SW5", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "IMM5_SW11", SO.Number));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "LACQUERING", SO.Number));

		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.EnrichedEvent.eventName());
		stream.setDescription(HellaVariables.EnrichedEvent.description());
		stream.setUri(sep.getUri() + "/hella-enriched");
		
		return stream;
	}

}
