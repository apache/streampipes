package de.fzi.cep.sepa.model;

import java.io.Serializable;
import java.util.UUID;

import com.clarkparsia.empire.SupportsRdfId;
import com.clarkparsia.empire.annotation.SupportsRdfIdImpl;


/**
 * top-level SEPA element 	
 */
public class AbstractSEPAElement implements SupportsRdfId, Serializable {

	private static final long serialVersionUID = -8593749314663582071L;

	private transient SupportsRdfIdImpl myId;
	
	/**
	 * the elementId, used as @RdfId for unnamed SEPA elements
	 */
	protected String elementId;
	
	public AbstractSEPAElement()
	{
		this.elementId = UUID.randomUUID().toString();	
		myId = new SupportsRdfIdImpl();
	}
	
	
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public RdfKey getRdfId() {
		// TODO Auto-generated method stub
		return myId.getRdfId();
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void setRdfId(RdfKey arg0) {
		myId.setRdfId(arg0);
		
	}
}
