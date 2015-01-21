package de.fzi.cep.sepa.model.client;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class SEPAClient extends ConsumableSEPAElement {
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<String> domains;
	
	private int inputNodes;
	
	
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

	public int getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(int inputNodes) {
		this.inputNodes = inputNodes;
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
