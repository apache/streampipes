package org.streampipes.model.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import org.streampipes.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:TransportFormat")
@Entity
public class TransportFormat extends UnnamedSEPAElement {
	
	private static final long serialVersionUID = -525073244975968386L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("rdf:type")
	protected List<URI> rdfType;

	public TransportFormat(String transportFormatType)
	{
		super();
		this.rdfType = new ArrayList<URI>();
		this.rdfType.add(URI.create(transportFormatType));
	}
	
	public TransportFormat(TransportFormat other)
	{
		super(other);
		this.rdfType = other.getRdfType();
	}
	
	public TransportFormat()
	{
		super();
		this.rdfType = new ArrayList<>();
	}

	public List<URI> getRdfType() {
		return rdfType;
	}

	public void setRdfType(List<URI> rdfType) {
		this.rdfType = rdfType;
	}

}
