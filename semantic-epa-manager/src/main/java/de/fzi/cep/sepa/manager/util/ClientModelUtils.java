package de.fzi.cep.sepa.manager.util;

import java.util.List;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;


public class ClientModelUtils {

	/**
	 * returns the root node of a partial pipeline (a pipeline without an action)
	 * @param pipeline
	 * @return @SEPAClient
	 * @throws Exception 
	 */
	
	public static SEPAClient getRootNode(Pipeline pipeline) throws Exception
	{
		List<SEPAClient> clients = pipeline.getSepas();
		for (SEPAClient client : pipeline.getSepas())
		{
			clients = remove(clients, client.getConnectedTo());
		}
		if (clients.size() != 1) throw new Exception();
		else return clients.get(0);
			
	}
	
	private static List<SEPAClient> remove(List<SEPAClient> sepas, List<String> domIds)
	{
		List<SEPAClient> result = sepas;
		for(String domId : domIds)
		{
			SEPAClient sepa = findSEPAbyId(sepas, domId);
			if (sepa != null) result.remove(sepa);
		}
		return result;
	}
	
	private static SEPAClient findSEPAbyId(List<SEPAClient> sepas, String domId)
	{
		for(SEPAClient sepa : sepas)
		{
			if (sepa.getDOM().equals(domId)) return sepa;
		}
		return null;
	}
	
	public static NamedSEPAElement transform(SEPAElement element)
	{
		if (element instanceof ActionClient) return ClientModelTransformer.fromSECClientModel(((ActionClient) element));
		else if (element instanceof SEPAClient) return ClientModelTransformer.fromSEPAClientModel(((SEPAClient) element));
		else if (element instanceof StreamClient) return ClientModelTransformer.fromStreamClientModel(((StreamClient) element));
		//exceptions
		return null;
	}
	
	public static StaticProperty getStaticPropertyById(String id, List<StaticProperty> staticProperties)
	{
		for(StaticProperty p : staticProperties)
		{
			if (p.getElementId().equals(id)) return p;
		}
		return null;
	}
	
	
}
