package de.fzi.cep.sepa.model;

import java.io.Serializable;

import com.clarkparsia.empire.SupportsRdfId;
import com.clarkparsia.empire.annotation.SupportsRdfIdImpl;


/**
 * top-level SEPA element 	
 */
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
