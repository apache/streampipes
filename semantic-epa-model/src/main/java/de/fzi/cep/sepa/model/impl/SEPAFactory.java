package de.fzi.cep.sepa.model.impl;

import java.util.List;

public class SEPAFactory {

	public static SEPA createSEPA(String uri, String name, String description, String pathName, List<Domain> domains)
	{
		return new SEPA(uri, name, description, pathName, domains);
	}
	
	
	public static EventStream createEventStream(String uri, String name, String description, List<EventProperty> eventProperties)
	{
		EventSchema schema = new EventSchema();
		schema.setEventProperties(eventProperties);
		EventStream stream = new EventStream(uri, name, description, schema);
		
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
