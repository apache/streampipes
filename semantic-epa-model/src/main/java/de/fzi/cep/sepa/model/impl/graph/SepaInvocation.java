package de.fzi.cep.sepa.model.impl.graph;

import java.io.Serializable;
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
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SEPAInvocationGraph")
@Entity
public class SepaInvocation extends InvocableSEPAElement implements Serializable {

	private static final long serialVersionUID = 865870355944824186L;


	@OneToOne (fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:produces")
	EventStream outputStream;
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOutputStrategy")
	List<OutputStrategy> outputStrategies;
	
	String pathName;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:epaType")
	protected List<String> epaTypes;
	
	public SepaInvocation(SepaDescription sepa)
	{
		super();
		this.setName(sepa.getName());
		this.setDescription(sepa.getDescription());
		this.setIconUrl(sepa.getIconUrl());
		this.setInputStreams(sepa.getEventStreams());
		this.setSupportedGrounding(sepa.getSupportedGrounding());
		this.setStaticProperties(sepa.getStaticProperties());
		this.setOutputStrategies(sepa.getOutputStrategies());
		this.setBelongsTo(sepa.getRdfId().toString());
		this.epaTypes = sepa.getEpaTypes();
		//this.setUri(belongsTo +"/" +getElementId());		
	}
	
	public SepaInvocation(SepaInvocation other)
	{
		super(other);
		this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
		if (other.getOutputStream() != null) this.outputStream =  new Cloner().stream(other.getOutputStream());
		this.pathName = other.getPathName();
		this.epaTypes = new Cloner().epaTypes(other.getEpaTypes());
	}

	public SepaInvocation(SepaDescription sepa, String domId)
	{
		this(sepa);
		this.domId = domId;
	}
	
	public SepaInvocation()
	{
		super();
		inputStreams = new ArrayList<EventStream>();
	}
	
	public SepaInvocation(String uri, String name, String description, String iconUrl, String pathName, List<EventStream> eventStreams, List<StaticProperty> staticProperties)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		this.inputStreams = eventStreams;
		this.staticProperties = staticProperties;
	}
	
	public SepaInvocation(String uri, String name, String description, String iconUrl, String pathName)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
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

	public List<String> getEpaTypes() {
		return epaTypes;
	}

	public void setEpaTypes(List<String> epaTypes) {
		this.epaTypes = epaTypes;
	}
	
}
