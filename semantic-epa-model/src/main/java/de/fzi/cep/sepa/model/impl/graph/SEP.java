package de.fzi.cep.sepa.model.impl.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.EventStream;

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
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasDomain")
	List<String> domains;
		
	public SEP() {
		super();
		eventStreams = new ArrayList<EventStream>();
		//empire needs this
	}
	
	public SEP(String uri, String name, String description, String iconUrl, List<EventStream> eventStreams, List<String> domains, EventSource eventSource)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = eventStreams;
		this.domains = domains;
	}
	
	public SEP(String uri, String name2, String description2, String iconUrl, List<String> domains, EventSource eventSource) {
		this(uri, name2, description2, iconUrl, new ArrayList<EventStream>(), domains, eventSource);
	}

	public List<EventStream> getEventStreams() {
		return eventStreams;
	}

	public void setEventStreams(List<EventStream> eventStreams) {
		this.eventStreams = eventStreams;
	}
	
	public void addEventStream(EventStream eventStream)
	{
		eventStreams.add(eventStream);
	}

	public EventSource getEventSource() {
		return eventSource;
	}

	public void setEventSource(EventSource eventSource) {
		this.eventSource = eventSource;
	}

	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
		this.domains = domains;
	}
	
	
			
}
