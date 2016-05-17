package de.fzi.cep.sepa.html.model;

import java.net.URI;

public abstract class Description {

	String name;
	String description;
	URI uri;
	
	public Description(String name, String description, URI uri)
	{
		this.name = name;
		this.description = description;
		this.uri = uri;
	}
	
	public Description()
	{
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
