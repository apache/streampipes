package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.EVENT_PROPERTY_LIST)
@Entity
public class EventPropertyList extends EventProperty {
	
	private static final long serialVersionUID = -2636018143426727534L;

	// TODO : change list<eventproperty> to eventproperty?

	@RdfProperty(StreamPipes.HAS_EVENT_PROPERTY)
	@OneToOne (fetch = FetchType.EAGER,
	   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<EventProperty> eventProperties;
	
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
	
}
