package de.fzi.cep.sepa.html.model;

import java.net.URI;

@Deprecated
public class AgentDescription extends Description {

	public AgentDescription(String name, String description, URI uri)
	{
		super(name, description, uri);
	}
	
	public AgentDescription()
	{
		super();
	}
		
}
