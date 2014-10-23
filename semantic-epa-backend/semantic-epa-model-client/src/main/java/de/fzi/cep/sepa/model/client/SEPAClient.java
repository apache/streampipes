package de.fzi.cep.sepa.model.client;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class SEPAClient extends SEPAElement {
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<String> domains;
	@OneToMany(cascade=CascadeType.ALL)
	private List<StaticProperty> staticProperties;
	private int inputNodes;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Map<String, String> mappingProperties;
	
	//private List<OutputStrategy<? extends OutputStrategyParameters>> outputStragegy;
	
	public SEPAClient(String name, String description, List<String> domains)
	{
		super(name, description);
		this.domains = domains;
	}
	
	public SEPAClient(String name, String description, List<String> domains, String iconName)
	{
		super(name, description, iconName);
		this.domains = domains;
	}


	public List<String> getDomains() {
		return domains;
	}


	public void setDomains(List<String> domains) {
		this.domains = domains;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public int getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(int inputNodes) {
		this.inputNodes = inputNodes;
	}

	public Map<String, String> getMappingProperties() {
		return mappingProperties;
	}

	public void setMappingProperties(Map<String, String> mappingProperties) {
		this.mappingProperties = mappingProperties;
	}

	/*
	public OutputStrategy getOutputStragegy() {
		return outputStragegy;
	}

	public void setOutputStragegy(OutputStrategy outputStragegy) {
		this.outputStragegy = outputStragegy;
	}
*/

}
