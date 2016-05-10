package de.fzi.cep.sepa.model.client.input;

public class SupportedProperty {

	private String elementId;
	
	private String propertyId;
	private String value;
	private boolean valueRequired;
	
	public SupportedProperty()
	{
		
	}
		
	public SupportedProperty(String elementId, String propertyId, String value,
			boolean valueRequired) {
		super();
		this.elementId = elementId;
		this.propertyId = propertyId;
		this.value = value;
		this.valueRequired = valueRequired;
	}

	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public String getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isValueRequired() {
		return valueRequired;
	}
	public void setValueRequired(boolean valueRequired) {
		this.valueRequired = valueRequired;
	}
	
	
}
