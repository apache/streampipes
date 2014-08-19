package de.fzi.cep.sepa.sources.samples.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.Domain;

public class Utils {
	
	public static List<String> createDomain(Domain...domains)
	{
		ArrayList<String> domainList = new ArrayList<String>();
		for(Domain d : domains)
			domainList.add(d.toString());
			
		return domainList;
	}
}
