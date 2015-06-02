package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:JmsTransportProtocol")
@Entity
public class JmsTransportProtocol extends TransportProtocol{

	public JmsTransportProtocol(String uri, int port, String topicName)
	{
		super(uri, port, topicName);
	}
	
	public JmsTransportProtocol() 
	{
		super();
	}
}
