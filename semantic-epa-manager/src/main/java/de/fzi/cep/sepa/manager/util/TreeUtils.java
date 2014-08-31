package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StreamClient;

public class TreeUtils {

	public static SEPAElement findSEPAElement(String id, List<SEPAClient> sepas, List<StreamClient> streams)
	{ 
		List<SEPAElement> allElements = new ArrayList<>();
		allElements.addAll(sepas);
		allElements.addAll(streams);
		
		for(SEPAElement element : allElements)
		{
			if (id.equals(element.getElementId())) return element;
		}
		
		return null;
	}
}
