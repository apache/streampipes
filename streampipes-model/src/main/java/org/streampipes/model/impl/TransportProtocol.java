package org.streampipes.model.impl;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass("sepa:TransportProtocol")
@Entity
@MappedSuperclass
public abstract class TransportProtocol extends UnnamedSEPAElement {
	
	private static final long serialVersionUID = 7625791395504335184L;

	@RdfProperty("sepa:brokerHostname")
	protected String brokerHostname;
	
	@RdfProperty("sepa:topic")
	protected String topicName;
	
	public TransportProtocol() {
		super();
	}
	
	public TransportProtocol(String uri, String topicName)
	{
		super();
		this.brokerHostname = uri;
		this.topicName = topicName;
	}

	public TransportProtocol(TransportProtocol other) {
		super(other);
		this.brokerHostname = other.getBrokerHostname();
		this.topicName = other.getTopicName();
	}

	public String getBrokerHostname() {
		return brokerHostname;
	}

	public void setBrokerHostname(String uri) {
		this.brokerHostname = uri;
	}
	
	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	@Override
	public String toString() {
		return brokerHostname;
	}
	
}
