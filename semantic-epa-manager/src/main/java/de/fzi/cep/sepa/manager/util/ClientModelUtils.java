package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.ConsumableSEPAElement;
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
	
	public static boolean isSepaInPipeline(Pipeline pipeline)
	{
		try {
			getRootNode(pipeline);
			return true;
		} catch (NoSepaInPipelineException e)
		{
			return false;
		}
	}
	
	public static ConsumableSEPAElement getRootNode(Pipeline pipeline) throws NoSepaInPipelineException
	{
		List<ConsumableSEPAElement> elements = new ArrayList<>();
		elements.addAll(pipeline.getSepas());
		
		if (pipeline.getAction() != null)
		{
			if (pipeline.getAction().getElementId() != null) elements.add(pipeline.getAction());
		}
		
		List<ConsumableSEPAElement> currentElements = new ArrayList<>();
		currentElements.addAll(pipeline.getSepas());
		if (pipeline.getAction() != null && pipeline.getAction().getElementId() != null) currentElements.add(pipeline.getAction());
		
		for (ConsumableSEPAElement client : currentElements)
		{
			elements = remove(elements, client.getConnectedTo());
		}
		if (elements.size() != 1) throw new NoSepaInPipelineException();
		else return elements.get(0);
			
	}
	
	private static List<ConsumableSEPAElement> remove(List<ConsumableSEPAElement> sepas, List<String> domIds)
	{
		List<ConsumableSEPAElement> result = sepas;
		for(String domId : domIds)
		{
			SEPAElement sepa = findSEPAbyId(sepas, domId);
			if (sepa != null) 	
				result = remove(result, sepa);
		}
		return result;
	}
	
	private static List<ConsumableSEPAElement> remove(List<ConsumableSEPAElement> clients,
			SEPAElement sepa) {
		List<ConsumableSEPAElement> result = new ArrayList<>();
		for(ConsumableSEPAElement client : clients)
		{
			if (!client.getDOM().equals(sepa.getDOM())) result.add(client);
		}
		return result;
	}

	private static ConsumableSEPAElement findSEPAbyId(List<ConsumableSEPAElement> sepas, String domId)
	{
		for(ConsumableSEPAElement sepa : sepas)
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
