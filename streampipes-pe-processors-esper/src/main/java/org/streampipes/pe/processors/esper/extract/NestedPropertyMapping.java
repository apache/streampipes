package org.streampipes.pe.processors.esper.extract;

public class NestedPropertyMapping {

	private String propertyName;
	private String fullPropertyName;
	
	public NestedPropertyMapping(String propertyName, String fullPropertyName) {
		super();
		this.propertyName = propertyName;
		this.fullPropertyName = fullPropertyName;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getFullPropertyName() {
		return fullPropertyName;
	}
	public void setFullPropertyName(String fullPropertyName) {
		this.fullPropertyName = fullPropertyName;
	}
	
	
}
