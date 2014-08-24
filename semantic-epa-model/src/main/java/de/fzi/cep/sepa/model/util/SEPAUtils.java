package de.fzi.cep.sepa.model.util;

import java.util.List;

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
	
	
	private static StaticProperty getStaticPropertyByName(List<StaticProperty> properties, String name)
	{
		for(StaticProperty p : properties)
		{
			if (p.getName().equals(name)) return p;
		}
		return null;
	}
}
