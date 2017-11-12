package org.streampipes.model.impl.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.ConsumableSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass("sepa:SemanticEventProcessingAgent")
@Entity
public class SepaDescription extends ConsumableSEPAElement {

	private static final long serialVersionUID = 3995767921861518597L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOutputStrategy")
	private List<OutputStrategy> outputStrategies;
	
	private String pathName;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:epaType")
	private List<String> category;

	public SepaDescription(SepaDescription other)
	{
		super(other);
		this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
		this.pathName = other.getPathName();
		this.category = new Cloner().epaTypes(other.getCategory());
	}
	
	public SepaDescription()
	{
		super();
		this.outputStrategies = new ArrayList<>();
		this.category = new ArrayList<>();
	}
	
	public SepaDescription(String uri, String name, String description, String iconUrl, List<EventStream> eventStreams, List<StaticProperty> staticProperties, List<OutputStrategy> outputStrategies)
	{
		super(uri, name, description, iconUrl);
		this.pathName = uri;
		this.eventStreams = eventStreams;
		this.staticProperties = staticProperties;
		this.outputStrategies = outputStrategies;
	}
	
	public SepaDescription(String pathName, String name, String description, String iconUrl)
	{
		super(pathName, name, description, iconUrl);
		this.pathName = pathName;
		eventStreams = new ArrayList<>();
		staticProperties = new ArrayList<>();
	}
	
	public SepaDescription(String pathName, String name, String description)
	{
		super(pathName, name, description, "");
		this.pathName = pathName;
		eventStreams = new ArrayList<>();
		staticProperties = new ArrayList<>();
	}

	

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}

}
