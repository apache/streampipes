package org.streampipes.sdk.stream;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;

public class SchemaBuilder {

	EventSchema schema;
	List<EventProperty> properties;
	
	private SchemaBuilder()
	{
		this.schema = new EventSchema();
		this.properties = new ArrayList<EventProperty>();	
	}
	
	public static SchemaBuilder create()
	{
		return new SchemaBuilder();
	}

	public SchemaBuilder simpleProperty(String label, String description, String runtimeName, String subPropertyOf, String dataType)
	{
		EventPropertyPrimitive primitive = new EventPropertyPrimitive(dataType, runtimeName, "", Utils.createURI(subPropertyOf));
		primitive.setDescription(description);
		primitive.setLabel(label);
		properties.add(primitive);
		return this;
	}
	
	public SchemaBuilder properties(List<EventProperty> properties)
	{
		this.properties.addAll(properties);
		return this;
	}
	
	public EventSchema build()
	{
		schema.setEventProperties(properties);
		return schema;
	}
	
	
}
