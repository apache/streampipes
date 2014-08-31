package de.fzi.cep.sepa.model.impl.graph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SEPAInvocationGraph")
@Entity
public class SEPAInvocationGraph extends NamedSEPAElement {

	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:receives")
	List<EventStream> inputStreams;
	
	
	@OneToOne (fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:produces")
	EventStream outputStream;
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasStaticProperty")
	List<StaticProperty> staticProperties;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOutputStrategy")
	List<OutputStrategy> outputStrategies;
	
	
	String pathName;
	List<String> domains;
	
	public SEPAInvocationGraph(SEPA sepa)
	{
		super();
		this.setName(sepa.getName());
		this.setDescription(sepa.getDescription());
		this.setDomains(sepa.getDomains());
		this.setIconUrl(sepa.getIconUrl());
		this.setInputStreams(sepa.getEventStreams());
		for(EventStream stream : this.getInputStreams())
		{
			System.out.println(stream.getRdfId().toString());
		}
		this.setStaticProperties(sepa.getStaticProperties());
		this.setOutputStrategies(sepa.getOutputStrategies());
		this.setUri(sepa.getRdfId().toString());
		
	}
	
	public SEPAInvocationGraph()
	{
		super();
		inputStreams = new ArrayList<EventStream>();
	}
	
	public SEPAInvocationGraph(String uri, String name, String description, String iconUrl, String pathName, List<String> domains, List<EventStream> eventStreams, List<StaticProperty> staticProperties)
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

	
	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
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

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}
	
	
	
	
	
}
