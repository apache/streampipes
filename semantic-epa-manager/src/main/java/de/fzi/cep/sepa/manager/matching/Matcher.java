package de.fzi.cep.sepa.manager.matching;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class Matcher {

	private List<EventProperty> allMatchingProperties = new ArrayList<>();
	private static final String sepaPrefix = "http://sepa.event-processing.org";
	private static final String rdfSchemaPrefix = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	
	public List<EventProperty> matchesProperties(EventProperty right,
			List<EventProperty> left) {
		List<EventProperty> matchingProperties = new ArrayList<>();
		for (EventProperty l : left) {
			if (matches(right, l))
				matchingProperties.add(l);
		}
		return allMatchingProperties;
	}
	
	public List<EventProperty> matchesPropertiesList(EventProperty right,
			List<EventProperty> left) {
		List<EventProperty> matchingProperties = new ArrayList<>();
		for (EventProperty l : left) {
			if (l instanceof EventPropertyList)
			{
				for(EventProperty sp : ((EventPropertyList) l).getEventProperties())
					if (matches(right, sp))
						matchingProperties.add(sp);
			}
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
					
					// check datatype restriction
					if (rightPrimitive.getRuntimeType() != null && !rightPrimitive.getRuntimeType().equals("")) {
						if (!leftPrimitive.getRuntimeType().equals(rightPrimitive.getRuntimeType())) 
							if (!subclassOf(leftPrimitive.getRuntimeType(), rightPrimitive.getRuntimeType())) match = false;
						System.out.println(match);
					}
					
					//check domain property restriction
					if (rightPrimitive.getDomainProperties() != null && rightPrimitive.getDomainProperties().size() > 0) {
						if (!rightPrimitive.getDomainProperties()
								.stream()
								.anyMatch(rp -> leftPrimitive
										.getDomainProperties()
										.stream()
										.anyMatch(lp -> 
											rp.toString().equals(lp.toString()))))
							match = false;
						System.out.println(match);
					}
							
					//if (!matches(leftUris, rightPrimitive.getDomainProperties())) match = false;
					if (match)
						allMatchingProperties.add(leftPrimitive);
					
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
	
	private boolean subclassOf(String left, String right) {
		if (!right.equals(SO.Number)) return false;
		else {
			if (left.equals(XSD._integer.toString())
					|| left.equals(XSD._long.toString()) || left.equals(XSD._double.toString()) || left.equals(XSD._float.toString())) return true;
		}
		return false;
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
		if (rightSubClasses == null) return match;
		for (URI uri : rightSubClasses) {
			if (!uri.toString().startsWith(sepaPrefix) && !uri.toString().startsWith(rdfPrefix) && !uri.toString().startsWith(rdfSchemaPrefix))
				relevantSubclasses.add(uri);
		}
		if (!leftSubClasses.stream().anyMatch(uri -> relevantSubclasses.contains(uri))) match = false;
		return match;
	}
}
