package de.fzi.cep.sepa.sources.samples.enriched;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class EnrichedUtils {

	public static EventSchema getEnrichedSchema()
	{
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(toEp(XSD._long, "time", "http://schema.org/Number", false));
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
		eventProperties.add(toEp(XSD._long, "ibop", MhWirth.Ibop));
		eventProperties.add(toEp(XSD._double, "hoist_press_A", MhWirth.HoistPressureA));
		eventProperties.add(toEp(XSD._double, "hoist_press_B", MhWirth.HoistPressureB));
		schema.setEventProperties(eventProperties);
		return schema;
	}
	
	private static EventPropertyPrimitive toEp(URI uri, String name, String type)
	{
		return new EventPropertyPrimitive(uri.toString(), name, "", de.fzi.cep.sepa.commons.Utils
				.createURI(type, SO.Number));
	}
	
	private static EventPropertyPrimitive toEp(URI uri, String name, String type, boolean number)
	{
		return new EventPropertyPrimitive(uri.toString(), name, "", de.fzi.cep.sepa.commons.Utils
				.createURI(type));
	}
}
