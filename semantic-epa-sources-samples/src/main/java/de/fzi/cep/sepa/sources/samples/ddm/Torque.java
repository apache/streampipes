package de.fzi.cep.sepa.sources.samples.ddm;

import java.util.ArrayList;
import java.util.List;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.commons.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class Torque implements EventStreamDeclarer{

	private String topicName;
	
	private static final Logger logger = LoggerFactory
			.getLogger("Torque");
	
	@Override
	public EventStream declareModel(SEP sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Text")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "value", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri(Configuration.TCP_SERVER_URL);
		grounding.setTopicName("SEPA.SEP.DDM.Torque");
		this.topicName = grounding.getTopicName();
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.DrillingTorque.eventName());
		stream.setDescription(AkerVariables.DrillingTorque.description());
		stream.setUri(sep.getUri() + "/torque");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Torque_Icon2" +"_HQ.png");
		
		return stream;
	}

	@Override
	public void executeStream() {
		// send POST request to event replay util
		// call some generic method which takes a source ID as a parameter and performs the request
		// AkerVariables.GearLubeOilTemperature.tagNumber returns tag number for this event stream
		// topicName denotes the actual topic to subscribe for
		
		long[] variables = { AkerVariables.DrillingTorque.tagNumber() };
		String cont = Utils.performRequest(variables, topicName, "121213123",
				"212342134");
		logger.info(cont);
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
}
