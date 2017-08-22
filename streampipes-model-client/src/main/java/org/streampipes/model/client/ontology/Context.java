package org.streampipes.model.client.ontology;

import java.io.InputStream;

public class Context {

	private InputStream inputStream;
	private String baseUri;
	
	private String contextId;
	
	private RdfFormat rdfFormat;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public RdfFormat getRdfFormat() {
		return rdfFormat;
	}

	public void setRdfFormat(RdfFormat rdfFormat) {
		this.rdfFormat = rdfFormat;
	}

	
}
