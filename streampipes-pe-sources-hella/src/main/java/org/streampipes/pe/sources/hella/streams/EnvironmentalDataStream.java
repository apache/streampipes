package org.streampipes.pe.sources.hella.streams;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.util.ArrayList;
import java.util.List;

public abstract class EnvironmentalDataStream extends AbstractHellaStream {

	public List<EventProperty> getPreparedProperties() {
		List<EventProperty> eventProperties = new ArrayList<>();
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "unit", SO.Text));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "location", SO.Text));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "sensorId", "", Utils.createURI(SO.Text)));
		
		return eventProperties;
	
	}
}
