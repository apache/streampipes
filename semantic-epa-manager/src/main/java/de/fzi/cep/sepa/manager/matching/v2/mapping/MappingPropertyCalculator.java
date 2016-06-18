package de.fzi.cep.sepa.manager.matching.v2.mapping;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.manager.matching.v2.PropertyMatch;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;

public class MappingPropertyCalculator {

	private List<EventProperty> allMatchingProperties;
	
	public MappingPropertyCalculator() {
		this.allMatchingProperties = new ArrayList<>();
	}
	
	public List<EventProperty> matchesProperties(List<EventProperty> offer,
			EventProperty requirement) {
		offer.forEach(of -> matches(of, requirement));
		return allMatchingProperties;		
	}
	
	public boolean matches(EventProperty offer, EventProperty requirement) {
		boolean match = true;
		if (requirement instanceof EventPropertyPrimitive) {
				if (offer instanceof EventPropertyList) match = false;
				else if (offer instanceof EventPropertyPrimitive) {
					if (new PropertyMatch().match(offer, requirement, new ArrayList<>()))
						allMatchingProperties.add(offer);		
				} else if (offer instanceof EventPropertyNested) {
					List<EventProperty> nestedProperties = ((EventPropertyNested) offer).getEventProperties();
					if (!matches(nestedProperties, requirement)) match = false;
				}
			} else if (requirement instanceof EventPropertyList)
			{
				if (!(offer instanceof EventPropertyList)) match = false;
				else {
					if (!matchesList((EventPropertyList) offer, (EventPropertyList) requirement)) match = false;
					else allMatchingProperties.add(offer);
				}
				
			} else if (requirement instanceof EventPropertyNested)
			{
				EventPropertyNested rightNested = (EventPropertyNested) requirement;
				for(EventProperty nestedProperty : rightNested.getEventProperties())
				{
					if (!matches(offer, nestedProperty)) match = false;
				}
			}
		return match;
	}
	
	public boolean matchesList(EventPropertyList offer, EventPropertyList requirement)
	{
		boolean match = true;
		for(EventProperty p : requirement.getEventProperties())
		{
			if (!matches(offer.getEventProperties(), p)) match = false;
		}
		return match;
	}
	
	public boolean matches(List<EventProperty> offer, EventProperty requirement)
	{
		boolean match = false;
		for(EventProperty of : offer)
		{
			if (matches(of, requirement)) match = true;
		}
		return match;
	}
	
	public List<EventProperty> matchesPropertiesList(List<EventProperty> offer, EventProperty requirement) {
		offer
			.stream()
			.filter(of -> (of instanceof EventPropertyList))
			.forEach(epl -> matches(epl, requirement));
		
		return allMatchingProperties;
	}
}
