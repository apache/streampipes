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
	
	private static final long serialVersionUID = 7625791395504335184L;

	@RdfProperty("sepa:brokerHostname")
	private String brokerHostname;
	
	@RdfProperty("sepa:topic")
	private String topicName;
	
	public TransportProtocol() {
		super();
	}
	
	public TransportProtocol(String uri, String topicName)
	{
		this.brokerHostname = uri;
		this.topicName = topicName;
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
	
}
