package de.fzi.cep.sepa.model.impl;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;


import javax.persistence.FetchType;
import javax.persistence.OneToMany;


import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

/**
 * class that represents Semantic Event Producers.
 *
 */
@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventProducer")
@Entity
public class SEP extends NamedSEPAElement {
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:produces")
	List<EventStream> eventStreams;	
	
	//@OneToOne(cascade = {CascadeType.ALL})
	//@RdfProperty("sepa:hasSource")
	EventSource eventSource;
	
	//@RdfProperty("sepa:hasDomain")
	List<Domain> domains;
	
	public SEP() {
		super();
		//empire needs this
	}
	
	public SEP(String uri, String name, String description, List<EventStream> eventStreams, List<Domain> domains, EventSource eventSource)
	{
		super(uri, name, description);
		this.eventStreams = eventStreams;
		this.domains = domains;
	}
	
	public SEP(String uri, String name2, String description2, List<Domain> domains, EventSource eventSource) {
		super(uri, name2, description2);
		this.domains = domains;
		this.eventSource = eventSource;
	}

	public List<EventStream> getEventStreams() {
		return eventStreams;
	}

	public void setEventStreams(List<EventStream> eventStreams) {
		this.eventStreams = eventStreams;
	}
			
}
