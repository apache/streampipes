package org.streampipes.pe.sources.mhwirth.ddm;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.MhWirth;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
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
import org.streampipes.commons.Utils;

public class SpeedShaft extends AbstractAlreadyExistingStream {

	@Override
	public EventStream declareModel(SepDescription sep) {

		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(),
				"variable_type", "", Utils
						.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(),
				"variable_timestamp", "", Utils
						.createURI("http://schema.org/DateTime")));
		eventProperties
				.add(new EventPropertyPrimitive(
						XSD._double.toString(),
						"value",
						"",
						Utils
								.createURI(MhWirth.Rpm, "http://schema.org/Number")));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.DrillingRPM.topic()));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.DrillingRPM.eventName());
		stream.setDescription(AkerVariables.DrillingRPM.description());
		stream.setUri(sep.getUri() + "/drillingRPM");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/DDM_Speed_Icon"
				+ "_HQ.png");

		return stream;
	}

}
