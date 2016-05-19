package de.fzi.cep.sepa.runtime.activity.detection.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class EnrichedUtils {

	@SuppressWarnings("static-access")
	public static EventStream getEnrichedStream() {
		return StreamBuilder.createStream(AkerVariables.Enriched.eventName(),
				AkerVariables.Enriched.description(), "/mhwirthenriched")
				.createStreamRestriction("http://schema.org/Number") //
				.createStreamRestriction(MhWirth.Rpm) //
				.createStreamRestriction(MhWirth.Torque) //
				.createStreamRestriction(MhWirth.HookLoad) //
				.createStreamRestriction(MhWirth.GearboxOilTemperature) //
				.createStreamRestriction(MhWirth.GearboxPressure) //
				.createStreamRestriction(MhWirth.SwivelOilTemperature) //
				.createStreamRestriction(MhWirth.Wob) //
				.createStreamRestriction(MhWirth.AmbientTemperature) //
				.createStreamRestriction(MhWirth.RamPosSetpoint) //
				.createStreamRestriction(MhWirth.RamPosMeasured) //
				.createStreamRestriction(MhWirth.RamVelSetpoint) //
				.createStreamRestriction(MhWirth.RamVelMeasured) //
				.createStreamRestriction(MhWirth.MruPos) //
				.createStreamRestriction(MhWirth.MruVel) //
				.createStreamRestriction(MhWirth.Ibop) //
				.createStreamRestriction(MhWirth.HoistPressureA) //
				.createStreamRestriction(MhWirth.HoistPressureB).build();
	}

	private static EventPropertyPrimitive toEp(URI uri, String name, String type) {
		return new EventPropertyPrimitive(uri.toString(), name, "",
				de.fzi.cep.sepa.commons.Utils.createURI(type, SO.Number));
	}

	private static EventPropertyPrimitive toEp(URI uri, String name, String type, boolean number) {
		return new EventPropertyPrimitive(uri.toString(), name, "", de.fzi.cep.sepa.commons.Utils.createURI(type));
	}
}
