package org.streampipes.storage.sparql;

import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.storage.controller.StorageManager;

import java.util.List;

public class TestSPARQLQuery {

	public static void main(String[] args)
	{
		//StorageUtils.fixEmpire();
		List<DataSourceDescription> seps = StorageManager.INSTANCE.getStorageAPI().getSEPsByDomain("DOMAIN_PERSONAL_ASSISTANT");
		System.out.println(seps.size());
		
	}
}
