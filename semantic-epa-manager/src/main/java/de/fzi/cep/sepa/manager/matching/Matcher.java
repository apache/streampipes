package de.fzi.cep.sepa.manager.matching;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;

public class Matcher {

	private List<EventProperty> allMatchingProperties = new ArrayList<>();
	private static final String sepaPrefix = "http://sepa.event-processing.org";
	private static final String rdfPrefix = "http://www.w3.org";
	
	public List<EventProperty> matchesProperties(EventProperty right,
			List<EventProperty> left) {
		List<EventProperty> matchingProperties = new ArrayList<>();
		for (EventProperty l : left) {
			if (matches(right, l))
				matchingProperties.add(l);
		}
		return allMatchingProperties;
	}
	
	public boolean matches(EventProperty right, List<EventProperty> left)
	{
		boolean match = false;
		for(EventProperty l : left)
		{
			if (matches(right, l)) match = true;
		}
		return match;
	}
	
	public boolean matches(EventProperty right, EventProperty left) {
		boolean match = true;
		//if (right.getClass() != left.getClass()) return false;
		if (right instanceof EventPropertyPrimitive)
			{
				EventPropertyPrimitive rightPrimitive = (EventPropertyPrimitive) right;

				if (left instanceof EventPropertyList) match = false;
				else if (left instanceof EventPropertyPrimitive)
				{
					EventPropertyPrimitive leftPrimitive = (EventPropertyPrimitive) left;
					List<URI> leftUris = leftPrimitive.getSubClassOf();
					if (!matches(leftUris, rightPrimitive.getSubClassOf())) match = false;
					else {
						allMatchingProperties.add(leftPrimitive);
					}
				} else if (left instanceof EventPropertyNested)
				{
					List<EventProperty> nestedProperties = ((EventPropertyNested) left).getEventProperties();
					if (!matches(right, nestedProperties)) match = false;
				}
			} else if (right instanceof EventPropertyList)
			{
				if (!(left instanceof EventPropertyList)) match = false;
				else {
					if (!matchesList((EventPropertyList) left, (EventPropertyList) right)) match = false;
					else allMatchingProperties.add(left);
				}
				
			} else if (right instanceof EventPropertyNested)
			{
				EventPropertyNested rightNested = (EventPropertyNested) right;
				for(EventProperty nestedProperty : rightNested.getEventProperties())
				{
					if (!matches(nestedProperty, left)) match = false;
				}
			}
		
		return match;
	}
	
	public boolean matchesList(EventPropertyList left, EventPropertyList right)
	{
		boolean match = true;
		for(EventProperty p : right.getEventProperties())
		{
			if (!matches(p, left.getEventProperties())) match = false;
		}
		return match;
	}
	
	public boolean matches(List<URI> leftSubClasses, List<URI> rightSubClasses)
	{
		List<URI> relevantSubclasses = new ArrayList<>();
		boolean match = true;
		for (URI uri : rightSubClasses) {
			if (!uri.toString().startsWith(sepaPrefix) && !uri.toString().startsWith(rdfPrefix))
				relevantSubclasses.add(uri);
		}
		if (!leftSubClasses.stream().anyMatch(uri -> relevantSubclasses.contains(uri))) match = false;
		return match;
	}
}
