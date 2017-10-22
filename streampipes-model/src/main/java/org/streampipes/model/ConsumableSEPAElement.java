package org.streampipes.model;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public abstract class ConsumableSEPAElement extends NamedSEPAElement {

	private static final long serialVersionUID = -6617391345752016449L;


	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:requires")
	protected List<EventStream> eventStreams;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasStaticProperty")
	protected List<StaticProperty> staticProperties;
	
	@OneToOne(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:supportedGrounding")
	EventGrounding supportedGrounding;
	
	public ConsumableSEPAElement()
	{
		super();
		this.eventStreams = new ArrayList<>();
		this.staticProperties = new ArrayList<>();
	}
	
	public ConsumableSEPAElement(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = new ArrayList<>();
		this.staticProperties = new ArrayList<>();
	}
	
	public ConsumableSEPAElement(ConsumableSEPAElement other) {
		super(other);
		if (other.getEventStreams() != null) this.eventStreams = new Cloner().streams(other.getEventStreams());
		this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
		if (other.getSupportedGrounding() != null) this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());	
	}

	public List<EventStream> getEventStreams() {
		return eventStreams;
	}

	public void setEventStreams(List<EventStream> eventStreams) {
		this.eventStreams = eventStreams;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}
	
	public boolean addEventStream(EventStream eventStream)
	{
		return eventStreams.add(eventStream);
	}

	public EventGrounding getSupportedGrounding() {
		return supportedGrounding;
	}

	public void setSupportedGrounding(EventGrounding supportedGrounding) {
		this.supportedGrounding = supportedGrounding;
	}
	
}
