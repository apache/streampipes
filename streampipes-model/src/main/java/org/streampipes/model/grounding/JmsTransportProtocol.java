package org.streampipes.model.grounding;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.JMS_TRANSPORT_PROTOCOL)
@Entity
public class JmsTransportProtocol extends TransportProtocol {

	private static final long serialVersionUID = -5650426611208789835L;
	
	@RdfProperty(StreamPipes.JMS_PORT)
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

	@Override
	public String toString() {
		return getBrokerHostname() + ":" + getPort();
	}
}
