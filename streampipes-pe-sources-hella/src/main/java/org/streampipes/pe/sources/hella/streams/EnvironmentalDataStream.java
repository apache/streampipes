package org.streampipes.pe.sources.hella.streams;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;

public abstract class EnvironmentalDataStream extends AbstractHellaStream {

	public List<EventProperty> getPreparedProperties() {
		List<EventProperty> eventProperties = new ArrayList<>();
		eventProperties.add(EpProperties.stringEp("unit", SO.Text));
		eventProperties.add(EpProperties.stringEp("location", SO.Text));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "sensorId", "", Utils.createURI(SO.Text)));
		
		return eventProperties;
	
	}
}
