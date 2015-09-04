package de.fzi.cep.sepa.sources.mhwirth.ram;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.mhwirth.config.AkerVariables;
import de.fzi.cep.sepa.sources.mhwirth.config.ProaSenseSettings;
import de.fzi.cep.sepa.sources.mhwirth.main.AbstractMhWirthStream;

public class RamVelocityMeasuredValue extends AbstractMhWirthStream {

	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI("http://schema.org/Text")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "", Utils.createURI(MhWirth.RamVelMeasured)));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.RamVelocityMeasuredValue.topic()));
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.RamVelocityMeasuredValue.eventName());
		stream.setDescription(AkerVariables.RamVelocityMeasuredValue.description());
		stream.setUri(sep.getUri() + "/ramVelocityMeasuredValue");
		
		return stream;
	}
}
