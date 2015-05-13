package de.fzi.cep.sepa.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

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
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:transportFormat")
	private List<TransportFormat> transportFormats;
	
	@RdfProperty("sepa:hasTopic")
	private String topicName;
	
	public EventGrounding()
	{
		super();
	}
	
	public EventGrounding(TransportProtocol transportProtocol, int port, String uri, TransportFormat transportFormat)
	{
		this.transportFormats = new ArrayList<>();
		this.transportFormats.add(transportFormat);
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

	public List<TransportFormat> getTransportFormats() {
		return transportFormats;
	}

	public void setTransportFormats(List<TransportFormat> transportFormats) {
		this.transportFormats = transportFormats;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	
	
}
