package de.fzi.cep.sepa.model.impl;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SEPA;

public class SEPAFactory {

	public static SEPA createSEPA(String uri, String name, String description, String iconUrl, String pathName, List<String> domains)
	{
		return new SEPA(uri, name, description, iconUrl, pathName, domains);
	}
	
	
	public static EventStream createEventStream(String uri, String name, String description, List<EventProperty> eventProperties)
	{
		EventSchema schema = new EventSchema();
		schema.setEventProperties(eventProperties);
		EventStream stream = new EventStream(uri, name, description, schema);
		
		return stream;
	}
	
	public static EventProperty createEventProperty(String name, String measurementUnit, String propertyType, List<URI> subClassOf)
	{
		return new EventPropertyPrimitive(propertyType, name, measurementUnit, subClassOf);
	}
	
	public static StaticProperty createStaticProperty(String name, String description, StaticPropertytype type)
	{
		if (type.equals(StaticPropertytype.Any)) return new AnyStaticProperty(name, description);
		else if (type.equals(StaticPropertytype.FreeText)) return new FreeTextStaticProperty(name, description);
		else return new OneOfStaticProperty(name, description);
	}
	
}
