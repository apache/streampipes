package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;

public class MappingUtils {

	public static List<StaticProperty> getMappingProperty(SEPA sepa, List<StaticProperty> staticProperties) throws Exception
	{
		List<StaticProperty> result = new ArrayList<StaticProperty>();		
		for(de.fzi.cep.sepa.model.impl.StaticProperty p : sepa.getStaticProperties())
		{
			if (p instanceof MappingProperty) result.add(findStaticProperty(p, staticProperties));
		}
		return result;
	}

	private static StaticProperty findStaticProperty(
			de.fzi.cep.sepa.model.impl.StaticProperty p,
			List<StaticProperty> staticProperties) throws Exception {
		for(StaticProperty s : staticProperties)
		{
			if (s.getElementId().equals(p.getRdfId().toString())) return s;
		}
		throw new Exception();
	}
}
