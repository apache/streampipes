package de.fzi.cep.sepa.sources.samples.ddm;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.sources.samples.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.ProaSenseSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class GearLubeOilTemperature implements EventStreamDeclarer {

	private static final Logger logger = LoggerFactory
			.getLogger("GearLubeOilTemperature");
	
	private String topicName;
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Text")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.GearLubeOilTemperature.topic()));
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		this.topicName = grounding.getTransportProtocol().getTopicName();

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.GearLubeOilTemperature.eventName());
		stream.setDescription(AkerVariables.GearLubeOilTemperature.description());
		stream.setUri(sep.getUri() + "/gearLubeTemp");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Temperature_Icon" +"_HQ.png");
		
		return stream;
	}

	@Override
	public void executeStream() {
		// send POST request to event replay util
		// call some generic method which takes a source ID as a parameter and performs the request
		// AkerVariables.GearLubeOilTemperature.tagNumber returns tag number for this event stream
		// topicName denotes the actual topic to subscribe for

        /*----------------Sample-Body---------------------------
        POST: http:// <host>/EventPlayer/api/playback/
        BODY (example):
        {
                "startTime": "2013-11-19T13:00:00+0100",
                "endTime" : "2013-11-19T14:15:00+0100" ,
                "variables" : ["1000692", "1002114"],
                "topic":"some_topic",
                "partner":"aker"
        }
        */

        long[] variables = {AkerVariables.GearLubeOilTemperature.tagNumber()};
        String cont = Utils.performRequest(variables, topicName, "121213123", "212342134");
        logger.info(cont);

	}

	@Override
	public boolean isExecutable() {
		return true;
	}
}
