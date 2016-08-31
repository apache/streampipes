package de.fzi.cep.sepa.model.impl.graph;

import java.util.ArrayList;
import java.util.Arrays;
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
import de.fzi.cep.sepa.model.util.Cloner;

/**
 * class that represents Semantic Event Producers.
 *
 */
@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventProducer")
@Entity
public class SepDescription extends NamedSEPAElement {
	
	private static final long serialVersionUID = 5607030219013954697L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:produces")
	List<EventStream> eventStreams;	
	
	EventSource eventSource;
		
	public SepDescription() {
		super();
		eventStreams = new ArrayList<EventStream>();
	}
	
	public SepDescription(SepDescription other)
	{
		super(other);
		this.eventStreams = new Cloner().streams(other.getEventStreams());
		this.eventStreams.forEach(e -> e.setCategory(Arrays.asList(this.getElementId())));
	}
	
	public SepDescription(String uri, String name, String description, String iconUrl, List<EventStream> eventStreams)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = eventStreams;
	}
	
	public SepDescription(String uri, String name2, String description2, String iconUrl) {
		this(uri, name2, description2, iconUrl, new ArrayList<EventStream>());
	}
	
	public SepDescription(String uri, String name, String description) {
		this(uri, name, description, "", new ArrayList<EventStream>());
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
}
