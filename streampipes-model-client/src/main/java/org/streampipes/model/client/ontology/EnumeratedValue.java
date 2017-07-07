package org.streampipes.model.client.ontology;

public class EnumeratedValue {

	private String elementName;
	private String runtimeValue;
	

	public EnumeratedValue(String elementName, String runtimeValue) {
		super();
		this.elementName = elementName;
		this.runtimeValue = runtimeValue;
	}
		
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	public String getRuntimeValue() {
		return runtimeValue;
	}
	public void setRuntimeValue(String runtimeValue) {
		this.runtimeValue = runtimeValue;
	}
	
	
}
