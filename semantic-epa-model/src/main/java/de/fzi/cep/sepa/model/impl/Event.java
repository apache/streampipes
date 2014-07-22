package de.fzi.cep.sepa.model.impl;

public class Event {

	EventSchema eventSchema;

	public Event(EventSchema eventSchema) {
		super();
		this.eventSchema = eventSchema;
	}

	public EventSchema getEventSchema() {
		return eventSchema;
	}

	public void setEventSchema(EventSchema eventSchema) {
		this.eventSchema = eventSchema;
	}
	
	
}
