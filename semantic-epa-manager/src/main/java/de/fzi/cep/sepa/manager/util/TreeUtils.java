package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class TreeUtils {

	/**
	 * 
	 * @param id the DOM ID
	 * @param sepas list of sepas in model-client format
	 * @param streams list of streams in model-client format
	 * @return a SEPA-client element
	 */
	
	public static SEPAElement findSEPAElement(String id, List<SEPAClient> sepas, List<StreamClient> streams)
	{ 
		List<SEPAElement> allElements = new ArrayList<>();
		allElements.addAll(sepas);
		allElements.addAll(streams);
		
		for(SEPAElement element : allElements)
		{
			if (id.equals(element.getDOM())) return element;
		}
		//TODO
		return null;
	}
	
	/**
	 *
	 * @param id the DOM ID
	 * @param graphs list of invocation graphs
	 * @return an invocation graph with a given DOM Id
	 */
	public static InvocableSEPAElement findByDomId(String id, List<InvocableSEPAElement> graphs)
	{
		for(InvocableSEPAElement graph : graphs)
		{
			if (graph.getDomId().equals(id)) 
				{
					return graph;
				}
		}
		//TODO
		return null;
	}
	
	public static EventProperty findEventProperty(String uri, List<EventStream> streams)
	{
		for(EventStream stream : streams)
		{
			for(EventProperty p : stream.getEventSchema().getEventProperties())
			{
				if (p.getRdfId().toString().equals(uri))
					return p;
			}
		}
		return null;
	}
	
	public static MappingProperty findMappingProperty(String elementId, ConsumableSEPAElement sepa)
	{
		for(de.fzi.cep.sepa.model.impl.StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}

	public static MappingProperty findMappingProperty(String elementId,
			SEPAInvocationGraph sepa) {
		for(de.fzi.cep.sepa.model.impl.StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
}
