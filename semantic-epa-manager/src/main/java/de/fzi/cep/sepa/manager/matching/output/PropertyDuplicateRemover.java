package de.fzi.cep.sepa.manager.matching.output;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;

public class PropertyDuplicateRemover {

	private List<EventProperty> existingProperties;
	private List<EventProperty> newProperties;
	
	public PropertyDuplicateRemover(List<EventProperty> existingProperties, List<EventProperty> newProperties) {
		this.existingProperties = existingProperties;
		this.newProperties = newProperties;
	}

	public List<EventProperty> rename() {
		
		List<EventProperty> newEventProperties = new ArrayList<EventProperty>();
		for(EventProperty p : newProperties)
		{
			int i = 1;
			EventProperty newProperty = p;
			while(isAlreadyDefined(existingProperties, newProperty))
			{
				if (newProperty instanceof EventPropertyPrimitive) 
					{
						EventPropertyPrimitive primitive = (EventPropertyPrimitive) newProperty;
						newProperty = new EventPropertyPrimitive(primitive.getPropertyType(), primitive.getRuntimeName() +i, primitive.getMeasurementUnit(), primitive.getSubClassOf());
						newProperty.setRdfId(new URIKey(URI.create(primitive.getRdfId().toString() +i)));
					}
				if (newProperty instanceof EventPropertyNested)
					{
						EventPropertyNested nested = (EventPropertyNested) newProperty;
						
						//TODO: hack
						List<EventProperty> nestedProperties = new ArrayList<>();
						
						for(EventProperty np : nested.getEventProperties())
						{
							if (np instanceof EventPropertyPrimitive)
							{
								EventPropertyPrimitive thisPrimitive = (EventPropertyPrimitive) np;
								EventProperty newNested = new EventPropertyPrimitive(thisPrimitive.getPropertyType(), thisPrimitive.getRuntimeName(), thisPrimitive.getMeasurementUnit(), thisPrimitive.getSubClassOf());	
								//newNested.setRdfId(new URIKey(URI.create("urn:fzi.de:sepa:" +UUID.randomUUID().toString())));
								newNested.setRdfId(new URIKey(URI.create(thisPrimitive.getRdfId().toString())));
								nestedProperties.add(newNested);
							}
								
						}
						newProperty = new EventPropertyNested(nested.getRuntimeName() +i, nestedProperties);
						//newProperty = new EventPropertyNested(nested.getPropertyName() +i, nested.getEventProperties());
						//newProperty.setRdfId(new URIKey(URI.create("urn:fzi.de:sepa:" +UUID.randomUUID().toString())));
						newProperty.setRdfId(new URIKey(URI.create(nested.getRdfId().toString() +i)));
					}
				i++;
			}
			newEventProperties.add(newProperty);
		}
		return newEventProperties;
	}
	
	private boolean isAlreadyDefined(List<EventProperty> existingProperties, EventProperty appendProperty)
	{
		for(EventProperty existingAppendProperty : existingProperties)
		{
			if (appendProperty.getRuntimeName().equals(existingAppendProperty.getRuntimeName()))
				return true;
		}
		return false;
	}
}
