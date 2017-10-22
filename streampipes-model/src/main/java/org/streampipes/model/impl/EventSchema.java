package org.streampipes.model.impl;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass("sepa:EventSchema")
@Entity
public class EventSchema extends UnnamedSEPAElement{

	private static final long serialVersionUID = -3994041794693686406L;
	
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

	public EventSchema(EventSchema other) {
		super(other);
		this.eventProperties = new Cloner().properties(other.getEventProperties());
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
	
	public Map<String, Object> toRuntimeMap()
	{
		return toUntypedRuntimeMap();
	}
	
	public Map<String, Object> toUntypedRuntimeMap()
	{
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		
		for(EventProperty p : this.getEventProperties())
		{
			propertyMap.putAll(p.getUntypedRuntimeFormat());
		}	
		return propertyMap;
	}
	
	public List<String> toPropertyList()
	{
		List<String> properties = new ArrayList<String>();
		
		for(EventProperty p : this.getEventProperties())
		{
			properties.addAll(p.getFullPropertyName(""));
		}
		return properties;
	}
	
	
}
