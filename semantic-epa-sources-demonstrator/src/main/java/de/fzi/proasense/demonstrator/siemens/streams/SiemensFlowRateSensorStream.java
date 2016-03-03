package de.fzi.proasense.demonstrator.siemens.streams;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.proasense.demonstrator.config.DemonstratorVariables;
import de.fzi.proasense.demonstrator.sources.AbstractDemonstratorStream;

public class SiemensFlowRateSensorStream extends AbstractDemonstratorStream {

	public SiemensFlowRateSensorStream(DemonstratorVariables variables) {
		super(variables);
	}

	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(variables.topic());

		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "timestamp", "",
				Utils.createURI("http://schema.org/DateTime")));
		// TODO do I need an id
//		eventProperties
//				.add(new EventPropertyPrimitive(XSD._string.toString(), "sensorId", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._float.toString(), "mass_flow", "",
				Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._float.toString(), "volume_flow", "",
				Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._float.toString(), "density", "",
				Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._float.toString(), "fluid_temperature", "",
				Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._float.toString(), "sensor_fault_flags", "",
				Utils.createURI(SO.Number)));
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(variables.eventName());
		stream.setDescription(variables.description());
		stream.setUri(sep.getUri() +"/" +variables.tagNumber());
		
		return stream;
	}

}
