package de.fzi.cep.sepa.model.builder;

import java.net.URI;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;

public class PrimitivePropertyBuilder {

	EventPropertyPrimitive primitive;
	
	private PrimitivePropertyBuilder(String dataType, String runtimeName, String subPropertyOf) {
		primitive = new EventPropertyPrimitive(dataType, runtimeName, "", Utils.createURI(subPropertyOf));
	}
	
	private PrimitivePropertyBuilder(String subPropertyOf) {
		primitive = new EventPropertyPrimitive(Utils.createURI(subPropertyOf));
	}
	
	public static PrimitivePropertyBuilder createProperty(URI dataType, String runtimeName, String subPropertyOf)
	{
		return new PrimitivePropertyBuilder(dataType.toString(), runtimeName, subPropertyOf);
	}
	
	public static PrimitivePropertyBuilder createPropertyRestriction(String subPropertyOf)
	{
		return new PrimitivePropertyBuilder("", "", subPropertyOf);
	}
	
	public PrimitivePropertyBuilder label(String label)
	{
		primitive.setLabel(label);
		return this;
	}
	
	public PrimitivePropertyBuilder description(String description)
	{
		return this;
	}
	
	public EventPropertyPrimitive build()
	{
		return primitive;
	}
	
}
