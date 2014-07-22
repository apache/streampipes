package de.fzi.cep.sepa.model.impl;

import java.util.List;

public class SEPAFactory {

	public static SEPA createSEPA(String name, String description, String pathName, List<Domain> domains)
	{
		return new SEPA(name, description, pathName, domains);
	}
	
	
	public static EventStream createEventStream(List<EventProperty> eventProperties)
	{
		EventSchema schema = new EventSchema();
		schema.setEventProperties(eventProperties);
		EventStream stream = new EventStream(schema);
		
		return stream;
	}
	
	public static EventProperty createEventProperty(String name, String measurementUnit, String propertyType)
	{
		return new EventProperty(propertyType, name, measurementUnit);
	}
	
	public static StaticProperty createStaticProperty(String name, String description, String type)
	{
		return new StaticProperty(name, description, type);
	}
	
}
