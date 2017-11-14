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
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.EVENT_PROPERTY_NESTED)
@Entity
public class EventPropertyNested extends EventProperty {

	private static final long serialVersionUID = 6565569954878135195L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_EVENT_PROPERTY)
	private List<EventProperty> eventProperties;
	
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

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}

}
