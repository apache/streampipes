package org.streampipes.model;


import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.empire.core.empire.annotation.SupportsRdfIdImpl;
import org.streampipes.empire.annotations.Namespaces;

import java.io.Serializable;


/**
 * top-level SEPA element 	
 */

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
				"dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "so", "http://schema.org/"})
public class AbstractSEPAElement implements SupportsRdfId, Serializable {

	private static final long serialVersionUID = -8593749314663582071L;

	private transient SupportsRdfIdImpl myId;
	
	protected RdfKey<String> rdfId;
	
	public AbstractSEPAElement()
	{
		myId = new SupportsRdfIdImpl();
	}
	
	public AbstractSEPAElement(AbstractSEPAElement other)
	{
		this();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public RdfKey getRdfId() {
		return myId.getRdfId();
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void setRdfId(RdfKey arg0) {
		myId.setRdfId(arg0);	
	}

}
