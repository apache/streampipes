package de.fzi.cep.sepa.model.impl;

import java.util.List;

import org.openrdf.model.Model;

public class SEP extends SEPAElement {

	List<EventStream> eventStreams;	
	List<Domain> domains;
	
	public SEP(String name, String description, List<EventStream> eventStreams, List<Domain> domains)
	{
		super(name, description);
		this.eventStreams = eventStreams;
		this.name = name;
		this.description = description;
		this.domains = domains;
	}
	
	public SEP(String name2, String description2, List<Domain> domains) {
		super(name2, description2);
	}

	public List<EventStream> getEventStream() {
		return eventStreams;
	}

	public void setEventStream(List<EventStream> eventStreams) {
		this.eventStreams = eventStreams;
	}
	
	public Model toModel()
	{
		return null;
	}
	
	public static SEP fromModel()
	{
		return null;
	}
	
	
}
