package de.fzi.cep.sepa.model.impl;

import java.util.List;

public class EventSchema {

	List<EventProperty> eventProperties;
	
	public EventSchema(List<EventProperty> eventProperties) {
		super();
		this.eventProperties = eventProperties;
	}
	
	public EventSchema()
	{
		super();
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}
	
	public boolean addEventProperty(EventProperty p)
	{
		return eventProperties.add(p);
	}
	
	
	
}
