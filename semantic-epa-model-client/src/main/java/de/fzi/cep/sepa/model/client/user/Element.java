package de.fzi.cep.sepa.model.client.user;

public class Element {

	private String elementId;
	private boolean publicElement;
	
	public Element(String elementId, boolean publicElement)
	{
		this.elementId = elementId;
		this.publicElement = publicElement;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public boolean isPublicElement() {
		return publicElement;
	}

	public void setPublicElement(boolean publicElement) {
		this.publicElement = publicElement;
	}
	
}
