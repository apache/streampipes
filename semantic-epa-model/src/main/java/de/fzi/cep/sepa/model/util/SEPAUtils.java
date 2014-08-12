package de.fzi.cep.sepa.model.util;

import de.fzi.cep.sepa.model.impl.SEPA;
import de.fzi.cep.sepa.model.impl.StaticProperty;

public class SEPAUtils {

	public static StaticProperty getStaticPropertyByName(SEPA sepa, String name)
	{
		for(StaticProperty p : sepa.getStaticProperties())
		{
			if (p.getName().equals(name)) return p;
		}
		return null;
	}
}
