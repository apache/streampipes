package de.fzi.cep.sepa.model.client.input;

public class Option {

	private String elementId;
	private String humanDescription;
	private boolean selected;
	
	public Option(String elementId, String humanDescription)
	{
		this.elementId = elementId;
		this.humanDescription = humanDescription;
	}
	
	public Option(String humanDescription)
	{
		this.humanDescription = humanDescription;
	}
	
	
	public String getElementId() {
		return elementId;
	}



	public void setElementId(String elementId) {
		this.elementId = elementId;
	}



	public String getHumanDescription() {
		return humanDescription;
	}

	public void setHumanDescription(String humanDescription) {
		this.humanDescription = humanDescription;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	
	
}
