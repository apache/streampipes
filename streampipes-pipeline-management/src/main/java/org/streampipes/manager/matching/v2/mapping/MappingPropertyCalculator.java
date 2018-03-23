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

package org.streampipes.manager.matching.v2.mapping;

import org.streampipes.manager.matching.v2.PropertyMatch;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;

public class MappingPropertyCalculator {

	private List<EventProperty> allMatchingProperties;
	
	public MappingPropertyCalculator() {
		this.allMatchingProperties = new ArrayList<>();
	}
	
	public List<EventProperty> matchesProperties(List<EventProperty> offer,
			EventProperty requirement) {
		offer.forEach(of -> matches(of, requirement, true));
		return allMatchingProperties;		
	}
	
	public boolean matches(EventProperty offer, EventProperty requirement, boolean addAsMatching) {
		boolean match = true;
		if (requirement instanceof EventPropertyPrimitive) {
				if (offer instanceof EventPropertyList) match = false;
				else if (offer instanceof EventPropertyPrimitive) {
					if (new PropertyMatch().match(offer, requirement, new ArrayList<>()))
						if (addAsMatching) allMatchingProperties.add(offer);
				} else if (offer instanceof EventPropertyNested) {
					List<EventProperty> nestedProperties = ((EventPropertyNested) offer).getEventProperties();
					if (!matches(nestedProperties, requirement)) match = false;
				}
			} else if (requirement instanceof EventPropertyList)
			{
				if (!(offer instanceof EventPropertyList)) match = false;
				else {
					if (!matchesList((EventPropertyList) offer, (EventPropertyList) requirement)) match = false;
					else if (addAsMatching) allMatchingProperties.add(offer);
				}
				
			} else if (requirement instanceof EventPropertyNested)
			{
				EventPropertyNested rightNested = (EventPropertyNested) requirement;
				for(EventProperty nestedProperty : rightNested.getEventProperties())
				{
					if (!matches(offer, nestedProperty, true)) match = false;
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
			if (matches(of, requirement, false)) match = true;
		}
		return match;
	}

}
