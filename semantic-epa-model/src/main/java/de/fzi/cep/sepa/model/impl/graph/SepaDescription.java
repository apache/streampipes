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

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventProcessingAgent")
@Entity
public class SepaDescription extends ConsumableSEPAElement {

	private static final long serialVersionUID = 3995767921861518597L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOutputStrategy")
	List<OutputStrategy> outputStrategies;
	
	String pathName;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasDomain")
	List<String> domains;
	
	public SepaDescription()
	{
		super();
		this.outputStrategies = new ArrayList<>();
	}
	
	public SepaDescription(String uri, String name, String description, String iconUrl, String pathName, List<String> domains, List<EventStream> eventStreams, List<StaticProperty> staticProperties, List<OutputStrategy> outputStrategies)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		this.eventStreams = eventStreams;
		this.staticProperties = staticProperties;
		this.domains = domains;
		this.outputStrategies = outputStrategies;
	}
	
	public SepaDescription(String uri, String name, String description, String iconUrl, String pathName, List<String> domains)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		this.domains = domains;
		eventStreams = new ArrayList<EventStream>();
		staticProperties = new ArrayList<StaticProperty>();
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

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}
		
}
