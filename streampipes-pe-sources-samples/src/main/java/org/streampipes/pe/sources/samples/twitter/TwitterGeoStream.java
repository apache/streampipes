package org.streampipes.pe.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.vocabulary.FOAF;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.quality.Accuracy;
import org.streampipes.model.impl.quality.EventPropertyQualityDefinition;
import org.streampipes.model.impl.quality.EventStreamQualityDefinition;
import org.streampipes.model.impl.quality.Frequency;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

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
				Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
				Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "latitude", "",
				Utils.createURI(SO.Latitude), latitudeQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "longitude", "",
				Utils.createURI(SO.Longitude), longitudeQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "userName", "",
				Utils.createURI(FOAF.Name)));

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

	}

	@Override
	public boolean isExecutable() {
		return false;
	}

}
