package org.streampipes.pe.sources.samples.ddm;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.MhWirth;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.commons.Utils;

public class GearboxPressure implements EventStreamDeclarer {
	
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI("http://schema.org/Text")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "", Utils.createURI(SO.Number, MhWirth.GearboxPressure)));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.GearBoxPressure.topic()));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
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
