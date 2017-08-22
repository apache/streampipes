package org.streampipes.pe.sources.hella.streams;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;

public class HellaEnrichedStream extends AbstractHellaStream {

	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(HellaVariables.EnrichedEvent.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "time", "", Utils.createURI(SO.Text)));
		eventProperties.add(EpProperties.integerEp("SW1_SW2", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW2_SW3", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW3_SW8", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW3_SW4", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW4_SW5", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW5_SW11", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW11_SW8", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW8_SW9", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW9_SW1", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW8_PM1", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW9_PM2", SO.Number));
		eventProperties.add(EpProperties.integerEp("PM1_SW1", SO.Number));
		eventProperties.add(EpProperties.integerEp("PM2_SW1", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW1_IMM1", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW2_IMM2", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW3_IMM3", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW4_IMM4", SO.Number));
		eventProperties.add(EpProperties.integerEp("SW5_IMM5", SO.Number));
		eventProperties.add(EpProperties.integerEp("IMM1_SW2", SO.Number));
		eventProperties.add(EpProperties.integerEp("IMM2_SW3", SO.Number));
		eventProperties.add(EpProperties.integerEp("IMM3_SW4", SO.Number));
		eventProperties.add(EpProperties.integerEp("IMM4_SW5", SO.Number));
		eventProperties.add(EpProperties.integerEp("IMM5_SW11", SO.Number));	
		eventProperties.add(EpProperties.integerEp("LACQUERING", SO.Number));		

		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.EnrichedEvent.eventName());
		stream.setDescription(HellaVariables.EnrichedEvent.description());
		stream.setUri(sep.getUri() + "/hella-enriched");
		
		return stream;
	}

}
