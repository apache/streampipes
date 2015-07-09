package de.fzi.cep.sepa.model.builder;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.staticproperty.AnyStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

public class SepaFactory {

	public static SepaDescription createSEPA(String uri, String name, String description, String iconUrl, String pathName, List<String> domains)
	{
		return new SepaDescription(uri, name, description, iconUrl, pathName, domains);
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
	
	public static StaticProperty createStaticProperty(String name, String description, StaticPropertyType type)
	{
		if (type.equals(StaticPropertyType.Any)) return new AnyStaticProperty(name, description);
		else if (type.equals(StaticPropertyType.FreeText)) return new FreeTextStaticProperty(name, description);
		else return new OneOfStaticProperty(name, description);
	}
	
}
