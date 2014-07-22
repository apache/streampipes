package de.fzi.cep.sepa.model;

import java.util.List;

import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.SEP;

public class SEPObjectFactory {

	public static SEP createSEP(String name, String description, List<Domain> domains)
	{
		return new SEP(name, description, domains);
	}
	
	public static EventStream createEventStream()
	{
		return new EventStream();
	}
	
	public static EventSchema createEventSchema(List<EventProperty> eventProperties)
	{
		return new EventSchema(eventProperties);
	}
	
	public static EventSchema createEmptyEventSchema()
	{
		return new EventSchema();
	}
	
	
}
