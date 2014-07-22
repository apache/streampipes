package de.fzi.cep.sepa.model.impl;


public class EventProperty {

	String propertyType;
	String propertyName;
	String measurementUnit;
	
	public EventProperty(String propertyType, String propertyName,
			String measurementUnit) {
		super();
		this.propertyType = propertyType;
		this.propertyName = propertyName;
		this.measurementUnit = measurementUnit;
	}
	
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getMeasurementUnit() {
		return measurementUnit;
	}
	public void setMeasurementUnit(String measurementUnit) {
		this.measurementUnit = measurementUnit;
	}
	
	
	
}
