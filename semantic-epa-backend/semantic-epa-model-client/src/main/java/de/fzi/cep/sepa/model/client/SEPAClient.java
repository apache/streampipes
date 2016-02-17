package de.fzi.cep.sepa.model.client;

import java.util.List;

import javax.persistence.Entity;

@Entity
public class SEPAClient extends ConsumableSEPAElement {
	
	private int inputNodes;
	
	public SEPAClient(String name, String description, List<String> category)
	{
		super(name, description, category);
	}
	
	public SEPAClient(String name, String description, String iconName, List<String> category)
	{
		super(name, description, iconName, category);
	}

	public int getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(int inputNodes) {
		this.inputNodes = inputNodes;
	}

}
