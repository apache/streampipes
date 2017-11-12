package org.streampipes.model.impl;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

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
	private List<EventProperty> eventProperties;
	
	public EventSchema(List<EventProperty> eventProperties) {
		super();
		this.eventProperties = eventProperties;
	}
	
	public EventSchema()
	{
		super();
		this.eventProperties = new ArrayList<>();
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
	
}
