package de.fzi.proasense.hella.streams;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.sdk.stream.EpProperties;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.proasense.hella.main.AbstractHellaStream;

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
