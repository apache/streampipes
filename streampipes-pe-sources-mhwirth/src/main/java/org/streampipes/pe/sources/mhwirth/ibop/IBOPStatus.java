package org.streampipes.pe.sources.mhwirth.ibop;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;
import org.streampipes.pe.sources.mhwirth.config.ProaSenseSettings;
import org.streampipes.pe.sources.mhwirth.config.SourcesConfig;

public class IBOPStatus extends AbstractAlreadyExistingStream {

	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "variable_value", "", Utils.createURI("http://sepa.event-processing.org/sepa#ibopStatus")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.Ibop.topic()));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.Ibop.eventName());
		stream.setDescription(AkerVariables.Ibop.description());
		stream.setUri(sep.getUri() + "/torque");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Temperature_Icon" +"_HQ.png");
		
		return stream;
	}

}
