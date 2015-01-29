package de.fzi.cep.sepa.model.impl;

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

import de.fzi.cep.sepa.model.UnnamedSEPAElement;
import de.fzi.cep.sepa.model.util.ModelUtils;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventSchema")
@Entity
public class EventSchema extends UnnamedSEPAElement{

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasEventProperty")
	List<EventProperty> eventProperties;
	
	public EventSchema(List<EventProperty> eventProperties) {
		super();
		this.eventProperties = eventProperties;
	}
	
	public EventSchema()
	{
		super();
		this.eventProperties = new ArrayList<EventProperty>();
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}
	
	
	public boolean addEventProperty(EventProperty p)
	{
		return eventProperties.add(p);
	}
	
	public Map<String, Class<?>> toRuntimeMap()
	{
		Map<String, Class<?>> propertyMap = new HashMap<String, Class<?>>();
		
		for(EventProperty p : this.getEventProperties())
		{
			propertyMap.put(p.getPropertyName(), ModelUtils.getPrimitiveClass(p.getPropertyType()));
		}
		
		return propertyMap;
	}
	
	public Map<String, Object> toUntypedRuntimeMap()
	{
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		
		for(EventProperty p : this.getEventProperties())
		{
			propertyMap.put(p.getPropertyName(), ModelUtils.getPrimitiveClass(p.getPropertyType()));
		}	
		return propertyMap;
	}
	
	public List<String> toPropertyList()
	{
		List<String> properties = new ArrayList<String>();
		
		for(EventProperty p : this.getEventProperties())
		{
			properties.add(p.getPropertyName());
		}
		return properties;
	}
	
	
}
