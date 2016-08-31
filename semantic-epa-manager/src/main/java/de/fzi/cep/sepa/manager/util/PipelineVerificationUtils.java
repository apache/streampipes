package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;

public class PipelineVerificationUtils {

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
	
	public static InvocableSEPAElement getRootNode(Pipeline pipeline) throws NoSepaInPipelineException
	{
		List<InvocableSEPAElement> elements = new ArrayList<>();
		elements.addAll(pipeline.getSepas());
		
		if (pipeline.getAction() != null)
		{
			if (pipeline.getAction().getElementId() != null) elements.add(pipeline.getAction());
		}
		
		List<InvocableSEPAElement> currentElements = new ArrayList<>();
		currentElements.addAll(pipeline.getSepas());
		if (pipeline.getAction() != null && pipeline.getAction().getElementId() != null) currentElements.add(pipeline.getAction());
		
		for (InvocableSEPAElement client : currentElements)
		{
			elements = remove(elements, client.getConnectedTo());
		}
		if (elements.size() != 1) throw new NoSepaInPipelineException();
		else return elements.get(0);
			
	}
	
	private static List<InvocableSEPAElement> remove(List<InvocableSEPAElement> sepas, List<String> domIds)
	{
		List<InvocableSEPAElement> result = sepas;
		for(String domId : domIds)
		{
			InvocableSEPAElement sepa = findSEPAbyId(sepas, domId);
			if (sepa != null) 	
				result = remove(result, sepa);
		}
		return result;
	}
	
	private static List<InvocableSEPAElement> remove(List<InvocableSEPAElement> clients,
			InvocableSEPAElement sepa) {
		List<InvocableSEPAElement> result = new ArrayList<>();
		for(InvocableSEPAElement client : clients)
		{
			if (!client.getDOM().equals(sepa.getDOM())) result.add(client);
		}
		return result;
	}

	private static InvocableSEPAElement findSEPAbyId(List<InvocableSEPAElement> sepas, String domId)
	{
		for(InvocableSEPAElement sepa : sepas)
		{
			if (sepa.getDOM().equals(domId)) return sepa;
		}
		return null;
	}

}
