package de.fzi.cep.sepa.model.client;

import javax.persistence.Entity;

@Entity
public class SEPAClient extends ConsumableSEPAElement {
	
	private int inputNodes;
	
	public SEPAClient(String name, String description)
	{
		super(name, description);
	}
	
	public SEPAClient(String name, String description, String iconName)
	{
		super(name, description, iconName);
	}

	public int getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(int inputNodes) {
		this.inputNodes = inputNodes;
	}

}
