package de.fzi.cep.sepa.model.client;

import java.util.List;
import java.util.Map;

public class SEPAClient extends SEPAElement {
	
	private List<Domain> domains;
	private List<StaticProperty> staticProperties;
	private int inputNodes;
	
	private Map<String, String> mappingProperties;
	
	private List<OutputStrategy<? extends OutputStrategyParameters>> outputStragegy;
	
	public SEPAClient(String name, String description, List<Domain> domains)
	{
		super(name, description);
		this.domains = domains;
	}
	
	public SEPAClient(String name, String description, List<Domain> domains, String iconName)
	{
		super(name, description, iconName);
		this.domains = domains;
	}


	public List<Domain> getDomains() {
		return domains;
	}


	public void setDomains(List<Domain> domains) {
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
