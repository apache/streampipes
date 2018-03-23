/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.util;

import org.streampipes.model.SpDataSequence;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.MatchingStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

public class TreeUtils {

	/**
	 * 
	 * @param id the DOM ID
	 * @param sepas list of sepas in model-client format
	 * @param streams list of streams in model-client format
	 * @return a SEPA-client element
	 */
	
	public static NamedStreamPipesEntity findSEPAElement(String id, List<DataProcessorInvocation> sepas, List<SpDataSequence>
					streams)
	{ 
		List<NamedStreamPipesEntity> allElements = new ArrayList<>();
		allElements.addAll(sepas);
		allElements.addAll(streams);
		
		for(NamedStreamPipesEntity element : allElements)
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
	public static InvocableStreamPipesEntity findByDomId(String id, List<InvocableStreamPipesEntity> graphs)
	{
		for(InvocableStreamPipesEntity graph : graphs)
		{
			if (graph.getDOM().equals(id))
				{
					return graph;
				}
		}
		//TODO
		return null;
	}
	
	public static EventProperty findEventProperty(String uri, List<SpDataStream> streams)
	{
		for(SpDataStream stream : streams)
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
	
	public static MappingProperty findMappingProperty(String elementId, ConsumableStreamPipesEntity sepa)
	{
		for(StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
	
	public static MatchingStaticProperty findMatchingProperty(String elementId, ConsumableStreamPipesEntity sepa)
	{
		for(StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MatchingStaticProperty) sp;
		}
		return null;
	}

	public static MappingProperty findMappingProperty(String elementId,
			DataProcessorInvocation sepa) {
		for(StaticProperty sp : sepa.getStaticProperties())
		{
			if (sp.getRdfId().toString().equals(elementId)) return (MappingProperty) sp;
		}
		return null;
	}
}
