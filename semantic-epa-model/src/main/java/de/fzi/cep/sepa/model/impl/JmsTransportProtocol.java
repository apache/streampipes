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

	private static final long serialVersionUID = -5650426611208789835L;
	
	@RdfProperty("sepa:jmsPort")
	private int port;
	
	public JmsTransportProtocol(String uri, int port, String topicName)
	{
		super(uri, topicName);
		this.port = port;
	}
	
	public JmsTransportProtocol(JmsTransportProtocol other)
	{
		super(other);
		this.port = other.getPort();
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
