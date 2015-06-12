package de.fzi.cep.sepa.model.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.impl.quality.EventPropertyQuality;
import de.fzi.cep.sepa.model.util.ModelUtils;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})
@RdfsClass("sepa:EventPropertyPrimitive")
@Entity
public class EventPropertyPrimitive extends EventProperty {

	@RdfProperty("sepa:hasPropertyType")
	String propertyType;
	
	@RdfProperty("sepa:hasMeasurementUnit")
	String measurementUnit;
	
	public EventPropertyPrimitive()
	{
		super();
	}
	
	public EventPropertyPrimitive(List<URI> subClassOf)
	{
		super(subClassOf);
	}

	public EventPropertyPrimitive(String propertyType, String propertyName,
			String measurementUnit, List<URI> subClassOf) {
		super(propertyName, subClassOf);
		this.propertyType = propertyType;
		this.measurementUnit = measurementUnit;
	}

	public EventPropertyPrimitive(String propertyType, String propertyName,
			String measurementUnit, List<URI> subClassOf, List<EventPropertyQuality> qualities) {
		super(propertyName, subClassOf, qualities);
		this.propertyType = propertyType;
		this.measurementUnit = measurementUnit;
	}
	
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	
	public String getMeasurementUnit() {
		return measurementUnit;
	}
	public void setMeasurementUnit(String measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	@Override
	public Map<String, Object> getRuntimeFormat() {
		return getUntypedRuntimeFormat();
	}

	@Override
	public Map<String, Object> getUntypedRuntimeFormat() {
		Map<String, Object> result = new HashMap<>();
		result.put(runtimeName, ModelUtils.getPrimitiveClass(propertyType));
		return result;
	}

	@Override
	public List<String> getFullPropertyName(String prefix) {
		List<String> result = new ArrayList<String>();
		result.add(prefix + runtimeName);
		return result;
	}
	
}
