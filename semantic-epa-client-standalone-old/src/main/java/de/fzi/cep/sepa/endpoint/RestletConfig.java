package de.fzi.cep.sepa.endpoint;

import org.restlet.Restlet;

public class RestletConfig {

	private Restlet restlet;
	private String uri;
	
	public RestletConfig(String uri, Restlet restlet) {
		super();
		this.restlet = restlet;
		this.uri = uri;
	}

	public Restlet getRestlet() {
		return restlet;
	}

	public String getUri() {
		return uri;
	}
		
}
