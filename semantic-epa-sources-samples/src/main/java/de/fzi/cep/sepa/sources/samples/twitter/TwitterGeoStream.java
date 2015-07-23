package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.vocabulary.FOAF;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.quality.Accuracy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class TwitterGeoStream implements EventStreamDeclarer {

	@Override
	public EventStream declareModel(SepDescription sep) {

		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> latitudeQualities = new ArrayList<EventPropertyQualityDefinition>();
		latitudeQualities.add(new Accuracy(25));

		List<EventPropertyQualityDefinition> longitudeQualities = new ArrayList<EventPropertyQualityDefinition>();
		longitudeQualities.add(new Accuracy(25));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "text", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "latitude", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Latitude), latitudeQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "longitude", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Longitude), longitudeQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "userName", "",
				de.fzi.cep.sepa.commons.Utils.createURI(FOAF.Name)));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency((float) 0.016666667));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Twitter.Geo"));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);
		stream.setName("Twitter Geo Stream");
		stream.setDescription("Twitter Geo Stream Description");
		stream.setUri(sep.getUri() + "/geo");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Tweet_Icon" + "_HQ.png");

		return stream;
	}

	@Override
	public void executeStream() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isExecutable() {
		return false;
	}

}
