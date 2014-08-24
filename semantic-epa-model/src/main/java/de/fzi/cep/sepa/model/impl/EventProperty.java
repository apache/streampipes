package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventProperty")
@Entity
public class EventProperty extends UnnamedSEPAElement {

	@RdfProperty("sepa:hasPropertyType")
	String propertyType;
	
	@RdfProperty("sepa:hasPropertyName")
	String propertyName;
	
	@RdfProperty("sepa:hasMeasurementUnit")
	String measurementUnit;
	
	public EventProperty()
	{
		super();
	}
	
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
