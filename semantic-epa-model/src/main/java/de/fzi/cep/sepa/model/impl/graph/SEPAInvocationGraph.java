package de.fzi.cep.sepa.model.impl.graph;

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

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SEPAInvocationGraph")
@Entity
public class SEPAInvocationGraph extends InvocableSEPAElement {
	
	@OneToOne (fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:produces")
	EventStream outputStream;
	
	
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
		this.setStaticProperties(sepa.getStaticProperties());
		this.setOutputStrategies(sepa.getOutputStrategies());
		this.setBelongsTo(sepa.getRdfId().toString());
		this.setUri(belongsTo +"/" +elementId);
		
	}
	
	public SEPAInvocationGraph(SEPA sepa, String domId)
	{
		this(sepa);
		this.domId = domId;
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
