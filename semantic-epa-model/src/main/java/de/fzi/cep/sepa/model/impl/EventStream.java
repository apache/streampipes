package de.fzi.cep.sepa.model.impl;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventStream")
@Entity
public class EventStream extends NamedSEPAElement {

	//@OneToMany(fetch = FetchType.EAGER,
	//		   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	//@RdfProperty("sepa:hasQuality")
	List<EventQuality> eventQuality;
	
	@RdfProperty("sepa:hasGrounding")
	EventGrounding eventGrounding;
	
	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasSchema")
	EventSchema eventSchema;
	
	
	public EventStream(String uri, String name, String description, List<EventQuality> eventQuality,
			EventGrounding eventGrounding, 
			EventSchema eventSchema) {
		super(uri, name, description);
		this.eventQuality = eventQuality;
		this.eventGrounding = eventGrounding;
		this.eventSchema = eventSchema;
	}
	
	
	public EventStream(String uri, String name, String description, EventSchema eventSchema)
	{
		this.eventSchema = eventSchema;
	}

	public EventStream() {
		super();
	}

	public List<EventQuality> getEventQuality() {
		return eventQuality;
	}

	public void setEventQuality(List<EventQuality> eventQuality) {
		this.eventQuality = eventQuality;
	}

	public EventSchema getEventSchema() {
		return eventSchema;
	}


	public void setEventSchema(EventSchema eventSchema) {
		this.eventSchema = eventSchema;
	}


	public EventGrounding getEventGrounding() {
		return eventGrounding;
	}


	public void setEventGrounding(EventGrounding eventGrounding) {
		this.eventGrounding = eventGrounding;
	}
	
}
