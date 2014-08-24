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
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SEPAInvocationGraph")
@Entity
public class SEPAInvocationGraph extends NamedSEPAElement {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:receives")
	List<EventStream> inputStreams;
	
	@RdfProperty("sepa:produces")
	EventStream outputStream;
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasStaticProperty")
	List<StaticProperty> staticProperties;
	
	
	String pathName;
	List<Domain> domains;
	
	public SEPAInvocationGraph()
	{
		super();
		inputStreams = new ArrayList<EventStream>();
	}
	
	public SEPAInvocationGraph(String uri, String name, String description, String iconUrl, String pathName, List<Domain> domains, List<EventStream> eventStreams, List<StaticProperty> staticProperties)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		this.inputStreams = eventStreams;
		this.staticProperties = staticProperties;
		//this.domains = domains;
	}
	
	public SEPAInvocationGraph(String uri, String name, String description, String iconUrl, String pathName, List<Domain> domains)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		//this.domains = domains;
		inputStreams = new ArrayList<EventStream>();
		staticProperties = new ArrayList<StaticProperty>();
	}
	
	public boolean addInputStream(EventStream eventStream)
	{
		return inputStreams.add(eventStream);
	}
	
	public boolean addStaticProperty(StaticProperty staticProperty)
	{
		return staticProperties.add(staticProperty);
	}

	
	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	
	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	public List<EventStream> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<EventStream> inputStreams) {
		this.inputStreams = inputStreams;
	}

	public EventStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(EventStream outputStream) {
		this.outputStream = outputStream;
	}
	
	
	
}
