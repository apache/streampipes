package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:TransportProtocol")
@Entity
@MappedSuperclass
public abstract class TransportProtocol extends UnnamedSEPAElement {

	@RdfProperty("sepa:hasPort")
	private int port;
	
	@RdfProperty("sepa:hasUri")
	private String uri;
	
	@RdfProperty("sepa:hasTopic")
	private String topicName;
	
	public TransportProtocol() {
		super();
	}
	
	public TransportProtocol(String uri, int port, String topicName)
	{
		this.uri = uri;
		this.port = port;
		this.topicName = topicName;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
}
