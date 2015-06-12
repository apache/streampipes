package de.fzi.cep.sepa.model.impl;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQuality;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventStream")
@Entity
public class EventStream extends NamedSEPAElement {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasStreamQuality")
	List<EventStreamQuality> eventStreamQualities;
	
	@OneToOne(fetch = FetchType.EAGER,
		   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasGrounding")
	EventGrounding eventGrounding;
	
	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasSchema")
	EventSchema eventSchema;
	
	
	public EventStream(String uri, String name, String description, String iconUrl, List<EventStreamQuality> eventStreamQualities,
			EventGrounding eventGrounding, 
			EventSchema eventSchema) {
		super(uri, name, description, iconUrl);
		this.eventStreamQualities = eventStreamQualities;
		this.eventGrounding = eventGrounding;
		this.eventSchema = eventSchema;
	}
	
	
	public EventStream(String uri, String name, String description, EventSchema eventSchema)
	{
		//super(uri, name, description);
		this.eventSchema = eventSchema;
	}

	public EventStream() {
		super();
	}

	public List<EventStreamQuality> getEventStreamQualities() {
		return eventStreamQualities;
	}


	public void setEventStreamQualities(
			List<EventStreamQuality> eventStreamQualities) {
		this.eventStreamQualities = eventStreamQualities;
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
