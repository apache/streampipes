package de.fzi.cep.sepa.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.clarkparsia.empire.annotation.RdfProperty;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

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
		this.staticProperties = new ArrayList<>();
	}
	
	public ConsumableSEPAElement(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.staticProperties = new ArrayList<>();
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
