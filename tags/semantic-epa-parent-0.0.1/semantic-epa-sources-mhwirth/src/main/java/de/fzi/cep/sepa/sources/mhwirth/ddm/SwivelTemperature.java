package de.fzi.cep.sepa.sources.mhwirth.ddm;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.impl.quality.MeasurementRange;
import de.fzi.cep.sepa.model.impl.quality.Resolution;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;
import de.fzi.cep.sepa.sources.mhwirth.config.AkerVariables;
import de.fzi.cep.sepa.sources.mhwirth.config.ProaSenseSettings;
import de.fzi.cep.sepa.sources.mhwirth.config.SourcesConfig;

public class SwivelTemperature extends AbstractAlreadyExistingStream {

	@Override
	public EventStream declareModel(SepDescription sep) {

		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> valueQualities = new ArrayList<EventPropertyQualityDefinition>();
		valueQualities.add(new MeasurementRange(-21, 1300));
		valueQualities.add(new Resolution((float) 0.1));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Number), valueQualities));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(100));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.SwivelOilTemperature.topic()));
		grounding
				.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);
		stream.setName(AkerVariables.SwivelOilTemperature.eventName());
		stream.setDescription(AkerVariables.SwivelOilTemperature.description());
		stream.setUri(sep.getUri() + "/swivelTemperature");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Temperature_Icon" + "_HQ.png");

		return stream;
	}

}
