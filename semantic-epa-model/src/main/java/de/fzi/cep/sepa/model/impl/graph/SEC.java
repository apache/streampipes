package de.fzi.cep.sepa.model.impl.graph;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventConsumer")
@Entity
public class SEC extends NamedSEPAElement{

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:requires")
	List<EventStream> eventStreams;
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasStaticProperty")
	List<StaticProperty> staticProperties;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasDomain")
	List<String> domains;
	
	public SEC(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = new ArrayList<>();
	}
	
	public SEC()
	{
		super();
	}

	public List<EventStream> getEventStreams() {
		return eventStreams;
	}

	public void setEventStreams(List<EventStream> eventStreams) {
		this.eventStreams = eventStreams;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
		this.domains = domains;
	}
	
	public boolean addEventStream(EventStream eventStream)
	{
		return eventStreams.add(eventStream);
	}
	
	
}
