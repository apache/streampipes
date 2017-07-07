package de.fzi.cep.sepa.model.impl.eventproperty;

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

import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})
@RdfsClass("sepa:EventPropertyNested")
@Entity
public class EventPropertyNested extends EventProperty {

	private static final long serialVersionUID = 6565569954878135195L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasEventProperty")
	List<EventProperty> eventProperties;
	
	public EventPropertyNested()
	{
		super();
	}
	
	public EventPropertyNested(EventPropertyNested other)
	{
		super(other);
		this.eventProperties = new Cloner().properties(other.getEventProperties());
	}
	
	public EventPropertyNested(String propertyName, List<EventProperty> eventProperties)
	{
		super(propertyName);
		this.eventProperties = eventProperties;
	}
	
	public EventPropertyNested(String propertyName) {
		super(propertyName);
		this.eventProperties = new ArrayList<>();
	
	}

	@Override
	public Map<String, Object> getRuntimeFormat() {
		return getUntypedRuntimeFormat();
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}

	@Override
	public Map<String, Object> getUntypedRuntimeFormat() {
		
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		Map<String, Object> subTypes = new HashMap<String, Object>();
		for(EventProperty p : eventProperties)
		{
			subTypes.putAll(p.getUntypedRuntimeFormat());
		}	
		propertyMap.put(runtimeName, subTypes);
		return propertyMap;
	}

	@Override
	public List<String> getFullPropertyName(String prefix) {
		
		List<String> result = new ArrayList<String>();
		for(EventProperty p : eventProperties)
		{
			result.addAll(p.getFullPropertyName(runtimeName +"."));
		}
		return result;
	}
}
