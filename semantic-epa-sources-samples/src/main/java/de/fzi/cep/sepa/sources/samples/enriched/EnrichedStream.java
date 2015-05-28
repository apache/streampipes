package de.fzi.cep.sepa.sources.samples.enriched;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class EnrichedStream implements EventStreamDeclarer{

	private String topicName;
	
	@Override
	public EventStream declareModel(SEP sep) {

		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(toEp(XSD._long, "timestamp", "http://schema.org/Number"));
		eventProperties.add(toEp(XSD._string, "eventName", "http://schema.org/Text"));
		eventProperties.add(toEp(XSD._double, "rpm", MhWirth.Rpm));
		eventProperties.add(toEp(XSD._double, "torque", MhWirth.Torque));
		eventProperties.add(toEp(XSD._double, "hook_load", MhWirth.HookLoad));
		eventProperties.add(toEp(XSD._double, "oil_temp_gearbox", MhWirth.GearboxOilTemperature));
		eventProperties.add(toEp(XSD._double, "pressure_gearbox", MhWirth.GearboxPressure));
		eventProperties.add(toEp(XSD._double, "oil_temp_swivel", MhWirth.SwivelOilTemperature));
		eventProperties.add(toEp(XSD._double, "wob", MhWirth.Wob));
		eventProperties.add(toEp(XSD._double, "temp_ambient", MhWirth.AmbientTemperature));
		eventProperties.add(toEp(XSD._double, "ram_pos_setpoint", MhWirth.RamPosSetpoint));
		eventProperties.add(toEp(XSD._double, "ram_pos_measured", MhWirth.RamPosMeasured));
		eventProperties.add(toEp(XSD._double, "ram_vel_setpoint", MhWirth.RamVelSetpoint));
		eventProperties.add(toEp(XSD._double, "ram_vel_measured", MhWirth.RamVelMeasured));
		eventProperties.add(toEp(XSD._double, "mru_pos", MhWirth.MruPos));
		eventProperties.add(toEp(XSD._double, "mru_vel", MhWirth.MruVel));
		eventProperties.add(toEp(XSD._double, "ibop", MhWirth.Ibop));
		eventProperties.add(toEp(XSD._double, "hoist_press_A", MhWirth.HoistPressureA));
		eventProperties.add(toEp(XSD._double, "hoist_press_B", MhWirth.HoistPressureB));
		
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri(Configuration.TCP_SERVER_URL);
		grounding.setTopicName(AkerVariables.Enriched.topic());
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		this.topicName = grounding.getTopicName();

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.Enriched.eventName());
		stream.setDescription(AkerVariables.Enriched.description());
		stream.setUri(sep.getUri() + "/mhwirthenriched");
		// stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Temperature_Icon"
		// +"_HQ.png");

		return stream;
	}

	@Override
	public void executeStream() {
		
		long[] variables = { AkerVariables.Enriched.tagNumber() };
		String cont = Utils.performRequest(variables, topicName, "121213123",
				"212342134");
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
	
	private EventPropertyPrimitive toEp(URI uri, String name, String type)
	{
		return new EventPropertyPrimitive(uri.toString(), name, "", de.fzi.cep.sepa.commons.Utils
				.createURI(type));
	}

}
