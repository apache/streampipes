package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventGrounding")
@Entity
public class EventGrounding extends UnnamedSEPAElement {

	
	private TransportProtocol transportProtocol;
	
	@RdfProperty("sepa:hasPort")
	private int port;
	
	@RdfProperty("sepa:hasUri")
	private String uri;
	
	private TransportFormat transportFormat;
	
	@RdfProperty("sepa:hasTopic")
	private String topicName;
	
	public EventGrounding()
	{
		super();
	}
	
	public EventGrounding(TransportProtocol transportProtocol, int port, String uri, TransportFormat transportFormat)
	{
		this.transportFormat = transportFormat;
		this.transportProtocol = transportProtocol;
		this.uri = uri;
		this.port = port;
	}

	public TransportProtocol getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocol = transportProtocol;
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

	public TransportFormat getTransportFormat() {
		return transportFormat;
	}

	public void setTransportFormat(TransportFormat transportFormat) {
		this.transportFormat = transportFormat;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	
	
}
