package de.fzi.cep.sepa.sources.samples.ddm;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.ProaSenseSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class GearboxPressure implements EventStreamDeclarer {
	
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Text")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "", de.fzi.cep.sepa.commons.Utils.createURI(SO.Number, MhWirth.GearboxPressure)));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.GearBoxPressure.topic()));
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.GearBoxPressure.eventName());
		stream.setDescription(AkerVariables.GearBoxPressure.description());
		stream.setUri(sep.getUri() + "/gearboxPressure");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/icon-gearbox" +".png");
		
		return stream;
	}

	@Override
	public void executeStream() {		
	}

	@Override
	public boolean isExecutable() {
		return false;
	}

}
