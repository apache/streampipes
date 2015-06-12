package de.fzi.cep.sepa.sources.samples.enriched;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

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
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.ProaSenseSettings;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class EnrichedStream implements EventStreamDeclarer{

	private String topicName;
	
	public JsonObject generateSampleEvent() {
		
		Random random = new Random();
		
		JsonObject obj = new JsonObject();
		obj.addProperty("timestamp", System.currentTimeMillis());
		obj.addProperty("eventName", "Cep");
		obj.addProperty("rpm", random.nextDouble());
		obj.addProperty("torque", random.nextDouble());
		obj.addProperty("hook_load", random.nextDouble());
		obj.addProperty("oil_temp_gearbox", random.nextDouble());
		obj.addProperty("pressure_gearbox", random.nextDouble());
		obj.addProperty("oil_temp_swivel", random.nextDouble());
		obj.addProperty("wob", random.nextDouble());
		obj.addProperty("temp_ambient", random.nextDouble());
		obj.addProperty("ram_pos_setpoint", random.nextDouble());
		obj.addProperty("ram_pos_measured", random.nextDouble());
		obj.addProperty("ram_vel_setpoint", random.nextDouble());
		obj.addProperty("ram_vel_measured", random.nextDouble());
		obj.addProperty("mru_pos", random.nextDouble());
		obj.addProperty("mru_vel", random.nextDouble());
		obj.addProperty("ibop", random.nextDouble());
		obj.addProperty("hoist_press_A", random.nextDouble());
		obj.addProperty("hoist_press_B", random.nextDouble());
		
		return obj;
	}
	
	
	@Override
	public EventStream declareModel(SepDescription sep) {

		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(toEp(XSD._long, "timestamp", "http://schema.org/Number", false));
		eventProperties.add(toEp(XSD._string, "eventName", "http://schema.org/Text", false));
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
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.Enriched.topic()));
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		this.topicName = grounding.getTransportProtocol().getTopicName();

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(AkerVariables.Enriched.eventName());
		stream.setDescription(AkerVariables.Enriched.description());
		stream.setUri(sep.getUri() + "/mhwirthenriched");

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
				.createURI(type, SO.Number));
	}
	
	private EventPropertyPrimitive toEp(URI uri, String name, String type, boolean number)
	{
		return new EventPropertyPrimitive(uri.toString(), name, "", de.fzi.cep.sepa.commons.Utils
				.createURI(type));
	}
}
