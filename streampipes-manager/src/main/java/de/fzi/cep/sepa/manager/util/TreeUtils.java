package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MatchingStaticProperty;

public class TreeUtils {

	/**
	 * 
	 * @param id the DOM ID
	 * @param sepas list of sepas in model-client format
	 * @param streams list of streams in model-client format
	 * @return a SEPA-client element
	 */
	
	public static NamedSEPAElement findSEPAElement(String id, List<SepaInvocation> sepas, List<EventStream> streams)
	{ 
		List<NamedSEPAElement> allElements = new ArrayList<>();
		allElements.addAll(sepas);
		allElements.addAll(streams);
		
		for(NamedSEPAElement element : allElements)
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
			if (graph.getDOM().equals(id))
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
				if (p instanceof EventPropertyList)
				{
					for(EventProperty sp : ((EventPropertyList) p).getEventProperties())
					{
						if (sp.getElementId().toString().equals(uri)) return sp;
					}
				}
				if (p.getElementId().toString().equals(uri))
					return p;
			}
		}
		return null;
	}
	
	public static MappingProperty findMappingProperty(String elementId, ConsumableSEPAElement sepa)
	{
		for(de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
	
	public static MatchingStaticProperty findMatchingProperty(String elementId, ConsumableSEPAElement sepa)
	{
		for(de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MatchingStaticProperty) sp;
		}
		return null;
	}

	public static MappingProperty findMappingProperty(String elementId,
			SepaInvocation sepa) {
		for(de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
}
