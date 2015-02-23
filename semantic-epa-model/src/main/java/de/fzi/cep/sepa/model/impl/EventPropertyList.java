package de.fzi.cep.sepa.model.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.util.ModelUtils;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})
@RdfsClass("sepa:EventPropertyList")
@Entity
public class EventPropertyList extends EventProperty {
	
	@RdfProperty("sepa:hasEventProperty")
	@OneToOne (fetch = FetchType.EAGER,
	   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	EventProperty eventProperty;
	
	public EventPropertyList()
	{
		super();
	}
	
	public EventPropertyList(String propertyName, EventProperty eventProperty) {
		super(propertyName);
		this.eventProperty = eventProperty;
	}

	public EventProperty getEventProperty() {
		return eventProperty;
	}

	public void setEventProperty(EventProperty eventProperty) {
		this.eventProperty = eventProperty;
	}

	@Override
	public Map<String, Object> getRuntimeFormat() {
		return getUntypedRuntimeFormat();
	}

	@Override
	public Map<String, Object> getUntypedRuntimeFormat() {
		Map<String, Object> result = new HashMap<>();
		System.out.println(propertyName);
		System.out.println(eventProperty.getPropertyName());
		if (eventProperty instanceof EventPropertyPrimitive) result.put(propertyName, ModelUtils.getPrimitiveClassAsArray(((EventPropertyPrimitive) eventProperty).getPropertyType()));
		else result.put(propertyName, ModelUtils.asList(eventProperty.getUntypedRuntimeFormat()));
		System.out.println("EVP" +eventProperty.getRuntimeFormat());
		return result;
	}

	@Override
	public List<String> getFullPropertyName(String prefix) {
		List<String> result = new ArrayList<String>();
		result.add(prefix + propertyName);
		return result;
	}
	
}
