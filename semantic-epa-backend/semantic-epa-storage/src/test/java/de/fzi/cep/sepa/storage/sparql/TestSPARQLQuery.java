package de.fzi.cep.sepa.storage.sparql;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.StorageUtils;

import java.util.List;

public class TestSPARQLQuery {

	public static void main(String[] args)
	{
		//StorageUtils.fixEmpire();
		List<SEP> seps = StorageManager.INSTANCE.getStorageAPI().getSEPsByDomain("DOMAIN_PERSONAL_ASSISTANT");
		System.out.println(seps.size());
		
	}
}
