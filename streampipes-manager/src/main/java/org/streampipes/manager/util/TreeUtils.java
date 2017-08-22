package org.streampipes.manager.util;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.ConsumableSEPAElement;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.MatchingStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;

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
		for(StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
	
	public static MatchingStaticProperty findMatchingProperty(String elementId, ConsumableSEPAElement sepa)
	{
		for(StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MatchingStaticProperty) sp;
		}
		return null;
	}

	public static MappingProperty findMappingProperty(String elementId,
			SepaInvocation sepa) {
		for(StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
}
