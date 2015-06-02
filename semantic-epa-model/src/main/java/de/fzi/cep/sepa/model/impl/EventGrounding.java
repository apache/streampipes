package de.fzi.cep.sepa.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
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

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasProtocol")
	private List<TransportProtocol> transportProtocols;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:transportFormat")
	private List<TransportFormat> transportFormats;
	
	public EventGrounding()
	{
		super();
	}
	
	public EventGrounding(TransportProtocol transportProtocol, TransportFormat transportFormat)
	{
		this.transportFormats = new ArrayList<>();
		this.transportFormats.add(transportFormat);
		this.transportProtocols = Arrays.asList(transportProtocol);
	}

	public List<TransportProtocol> getTransportProtocols() {
		return transportProtocols;
	}

	public void setTransportProtocols(List<TransportProtocol> transportProtocols) {
		this.transportProtocols = transportProtocols;
	}

	public void setTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocols = Arrays.asList(transportProtocol);
	}
	
	public TransportProtocol getTransportProtocol() {
		return transportProtocols.get(0);
	}
	
	public List<TransportFormat> getTransportFormats() {
		return transportFormats;
	}

	public void setTransportFormats(List<TransportFormat> transportFormats) {
		this.transportFormats = transportFormats;
	}
	
}
