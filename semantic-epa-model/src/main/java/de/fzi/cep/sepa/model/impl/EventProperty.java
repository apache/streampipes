package de.fzi.cep.sepa.model.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfId;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})
@RdfsClass("sepa:EventProperty")
@Entity
public class EventProperty extends UnnamedSEPAElement {

	private static final String prefix = "urn:fzi.de:sepa:";
	
	String propertyId;
	
	@RdfProperty("sepa:hasPropertyType")
	String propertyType;
	
	@RdfProperty("sepa:hasPropertyName")
	String propertyName;
	
	@RdfProperty("sepa:hasMeasurementUnit")
	String measurementUnit;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("rdf:type")
	List<URI> subClassOf;
	
	public EventProperty()
	{
		super(prefix + UUID.randomUUID().toString());
	}
	
	public EventProperty(List<URI> subClassOf)
	{
		super(prefix + UUID.randomUUID().toString());
		this.subClassOf = subClassOf;
	}
	
	public EventProperty(String propertyType, String propertyName,
			String measurementUnit, List<URI> subClassOf) {
		super(prefix + UUID.randomUUID().toString());
		this.propertyType = propertyType;
		this.propertyName = propertyName;
		this.measurementUnit = measurementUnit;
		this.subClassOf = subClassOf;
		
		
		
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

	public List<URI> getSubClassOf() {
		return subClassOf;
	}

	public void setSubClassOf(List<URI> subClassOf) {
		this.subClassOf = subClassOf;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	
	
}
