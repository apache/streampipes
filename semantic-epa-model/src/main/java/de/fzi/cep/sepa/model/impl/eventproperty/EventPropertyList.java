package de.fzi.cep.sepa.model.impl.eventproperty;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import de.fzi.cep.sepa.model.util.Cloner;
import de.fzi.cep.sepa.model.util.ModelUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})
@RdfsClass("sepa:EventPropertyList")
@Entity
public class EventPropertyList extends EventProperty {
	
	private static final long serialVersionUID = -2636018143426727534L;

	// TODO : change list<eventproperty> to eventproperty?

	@RdfProperty("sepa:hasEventProperty")
	@OneToOne (fetch = FetchType.EAGER,
	   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	protected List<EventProperty> eventProperties;
	
	public EventPropertyList()
	{
		super();
		eventProperties = new ArrayList<>();
	}
	
	public EventPropertyList(EventPropertyList other)
	{
		super(other);
		this.eventProperties = new Cloner().properties(other.getEventProperties());
	}
	
	public EventPropertyList(String propertyName, EventProperty eventProperty) {
		super(propertyName);
		eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(eventProperty);
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}

	@Override
	public Map<String, Object> getRuntimeFormat() {
		return getUntypedRuntimeFormat();
	}

	@Override
	public Map<String, Object> getUntypedRuntimeFormat() {
		Map<String, Object> result = new HashMap<>();
		for(EventProperty p : eventProperties)
		{
			if (p instanceof EventPropertyPrimitive && eventProperties.size() == 1) 
				{
					result.put(runtimeName, ModelUtils.getPrimitiveClassAsArray(((EventPropertyPrimitive) p).getRuntimeType()));
					break;
				}
			else 
				result.put(runtimeName, ModelUtils.asList(p.getUntypedRuntimeFormat()));
		}
		return result;
	}

	@Override
	public List<String> getFullPropertyName(String prefix) {
		List<String> result = new ArrayList<String>();
		result.add(prefix + runtimeName);
		return result;
	}
	
}
