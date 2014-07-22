package de.fzi.cep.sepa.model.impl;

import java.util.ArrayList;
import java.util.List;

public class SEPA extends SEPAElement{

	List<EventStream> eventStreams;
	List<StaticProperty> staticProperties;
	String pathName;
	List<Domain> domains;
	
	public SEPA(String name, String description, String pathName, List<Domain> domains, List<EventStream> eventStreams, List<StaticProperty> staticProperties)
	{
		super(name, description);
		this.pathName = pathName;
		this.eventStreams = eventStreams;
		this.staticProperties = staticProperties;
		this.domains = domains;
	}
	
	public SEPA(String name, String description, String pathName, List<Domain> domains)
	{
		super(name, description);
		this.pathName = pathName;
		this.domains = domains;
		eventStreams = new ArrayList<EventStream>();
		staticProperties = new ArrayList<StaticProperty>();
	}

	public List<EventStream> getEventStreams() {
		return eventStreams;
	}

	public void setEventStreams(List<EventStream> eventStreams) {
		this.eventStreams = eventStreams;
	}
	
	public boolean addEventStream(EventStream eventStream)
	{
		return eventStreams.add(eventStream);
	}
	
	public boolean addStaticProperty(StaticProperty staticProperty)
	{
		return staticProperties.add(staticProperty);
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}
	
	
	
	
	
}
