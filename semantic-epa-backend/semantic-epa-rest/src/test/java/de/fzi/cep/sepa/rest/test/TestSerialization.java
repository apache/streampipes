package de.fzi.cep.sepa.rest.test;

import java.util.List;

import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;

public class TestSerialization {

	public static void main(String[] args)
	{
		List<SepaDescription> sepas;
		sepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
		
		System.out.println(sepas.size());
		
		String json = Utils.getGson().toJson(ClientModelTransformer.toSEPAClientModel(sepas.get(0)));
		System.out.println(json);
		
		SEPAClient sepa = Utils.getGson().fromJson(json, SEPAClient.class);
		System.out.println(sepa.getElementId());
	}
}
