package org.streampipes.model.impl.eventproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.model.util.ModelUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

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
