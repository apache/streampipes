package org.streampipes.sdk.stream;

import org.streampipes.model.schema.EventProperty;

public abstract class PropertyBuilder {

	EventProperty property;
	PropertyBuilder builder;
	SchemaBuilder schemaBuilder;
	
	protected PropertyBuilder(PropertyBuilder builder)
	{
		this.builder = builder;
	}
	
	public PropertyBuilder humanReadableTitle(String humanReadableTitle)
	{
		property.setLabel(humanReadableTitle);
		return builder;
	}
	
	public PropertyBuilder humanReadableDescription(String humanReadableDescription)
	{
		property.setDescription(humanReadableDescription);
		return builder;
	}
	
	public PropertyBuilder runtimeName(String runtimeName)
	{
		property.setRuntimeName(runtimeName);
		return builder;
	}
}
