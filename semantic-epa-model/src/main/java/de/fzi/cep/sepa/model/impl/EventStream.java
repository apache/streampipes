package de.fzi.cep.sepa.model.impl;

import java.util.List;

public class EventStream {

	List<EventQuality> eventQuality;
	List<EventGrounding> eventGrounding;
	List<EventSource> eventSource;
	EventSchema eventSchema;
	
	
	public EventStream(List<EventQuality> eventQuality,
			List<EventGrounding> eventGrounding, List<EventSource> eventSource,
			EventSchema eventSchema) {
		super();
		this.eventQuality = eventQuality;
		this.eventGrounding = eventGrounding;
		this.eventSource = eventSource;
		this.eventSchema = eventSchema;
	}
	
	
	public EventStream(EventSchema eventSchema)
	{
		this.eventSchema = eventSchema;
	}

	public EventStream() {
		
	}


	public List<EventQuality> getEventQuality() {
		return eventQuality;
	}

	public void setEventQuality(List<EventQuality> eventQuality) {
		this.eventQuality = eventQuality;
	}

	public List<EventGrounding> getEventGrounding() {
		return eventGrounding;
	}

	public void setEventGrounding(List<EventGrounding> eventGrounding) {
		this.eventGrounding = eventGrounding;
	}

	public List<EventSource> getEventSource() {
		return eventSource;
	}

	public void setEventSource(List<EventSource> eventSource) {
		this.eventSource = eventSource;
	}


	public EventSchema getEventSchema() {
		return eventSchema;
	}


	public void setEventSchema(EventSchema eventSchema) {
		this.eventSchema = eventSchema;
	}


	
	
	
	
}
