package de.fzi.cep.sepa.model.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class SEPAUtils {

	public static StaticProperty getStaticPropertyByName(SEPA sepa, String name)
	{
		return getStaticPropertyByName(sepa.getStaticProperties(), name);
	}
	
	public static StaticProperty getStaticPropertyByName(SEPAInvocationGraph seg, String name)
	{
		return getStaticPropertyByName(seg.getStaticProperties(), name);
	}
	
	// TODO: fetch event property from db for given static property name
	public static String getMappingPropertyName(SEPAInvocationGraph sepa, String staticPropertyName)
	{
		URI propertyURI = getURIFromStaticProperty(sepa, staticPropertyName);
		
		for(EventStream stream : sepa.getInputStreams())
		{
			for(EventProperty p : stream.getEventSchema().getEventProperties())
			{
				if (p.getRdfId().toString().equals(propertyURI.toString())) return p.getPropertyName();
			}
		}
		return null;
		//TODO: exceptions
	}
	
	private static URI getURIFromStaticProperty(SEPAInvocationGraph sepa, String staticPropertyName)
	{
		for(StaticProperty p : sepa.getStaticProperties())
		{
			
			if (p instanceof MappingProperty)
			{
				MappingProperty mp = (MappingProperty) p;
				if (mp.getName().equals(staticPropertyName)) return mp.getMapsTo();
			}
		}
		return null;
		//TODO: exceptions
	}
	
	public static URI getURIbyPropertyName(EventStream stream, String propertyName)
	{
		for(EventProperty p : stream.getEventSchema().getEventProperties())
		{
			if (p.getPropertyName().equals(propertyName))
				try {
					return new URI(p.getRdfId().toString());
				} catch (URISyntaxException e) {
					return null;
				}
		}
		return null;
		//TODO exceptions
	}
	
	
	private static StaticProperty getStaticPropertyByName(List<StaticProperty> properties, String name)
	{
		for(StaticProperty p : properties)
		{
			if (p.getName().equals(name)) return p;
		}
		return null;
	}

	public static String getOneOfProperty(SEPAInvocationGraph sepa,
			String staticPropertyName) {
		for(StaticProperty p : sepa.getStaticProperties())
		{
			if (p.getName().equals(staticPropertyName))
			{
				if (p instanceof OneOfStaticProperty)
				{
					OneOfStaticProperty thisProperty = (OneOfStaticProperty) p;
					for(Option option : thisProperty.getOptions())
					{
						if (option.isSelected()) return option.getName();
					}
				}
			}
		}
		return null;
		//TODO exceptions
	}
}
