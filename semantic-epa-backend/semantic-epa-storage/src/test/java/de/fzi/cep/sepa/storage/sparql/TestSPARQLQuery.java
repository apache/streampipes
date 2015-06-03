package de.fzi.cep.sepa.storage.sparql;

import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.StorageUtils;

import java.util.List;

public class TestSPARQLQuery {

	public static void main(String[] args)
	{
		//StorageUtils.fixEmpire();
		List<SepDescription> seps = StorageManager.INSTANCE.getStorageAPI().getSEPsByDomain("DOMAIN_PERSONAL_ASSISTANT");
		System.out.println(seps.size());
		
	}
}
