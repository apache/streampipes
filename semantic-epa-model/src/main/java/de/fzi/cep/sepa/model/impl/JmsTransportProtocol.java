package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:JmsTransportProtocol")
@Entity
public class JmsTransportProtocol extends TransportProtocol{

	@RdfProperty("sepa:jmsPort")
	private int port;
	
	public JmsTransportProtocol(String uri, int port, String topicName)
	{
		super(uri, topicName);
		this.port = port;
	}
	
	public JmsTransportProtocol() 
	{
		super();
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
