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
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventStream")
@Entity
public class EventStream extends NamedSEPAElement {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasEventStreamQualityDefinition")
	List<EventStreamQualityDefinition> hasEventStreamQualities;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasEventStreamQualityRequirement")
	List<EventStreamQualityRequirement> requiresEventStreamQualities;

	@OneToOne(fetch = FetchType.EAGER,
		   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasGrounding")
	EventGrounding eventGrounding;
	
	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasSchema")
	EventSchema eventSchema;
	
	
	public EventStream(String uri, String name, String description, String iconUrl, List<EventStreamQualityDefinition> hasEventStreamQualities,
			EventGrounding eventGrounding, 
			EventSchema eventSchema) {
		super(uri, name, description, iconUrl);
		this.hasEventStreamQualities = hasEventStreamQualities;
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


	public List<EventStreamQualityDefinition> getHasEventStreamQualities() {
		return hasEventStreamQualities;
	}


	public void setHasEventStreamQualities(
			List<EventStreamQualityDefinition> hasEventStreamQualities) {
		this.hasEventStreamQualities = hasEventStreamQualities;
	}
	
	

	
	public List<EventStreamQualityRequirement> getRequiresEventStreamQualities() {
		return requiresEventStreamQualities;
	}


	public void setRequiresEventStreamQualities(
			List<EventStreamQualityRequirement> requiresEventStreamQualities) {
		this.requiresEventStreamQualities = requiresEventStreamQualities;
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
