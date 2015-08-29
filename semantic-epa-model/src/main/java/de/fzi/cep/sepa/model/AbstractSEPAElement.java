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
	
	protected RdfKey<String> rdfId;
	
	/**
	 * the elementId, used as @RdfId for unnamed SEPA elements
	 */
	protected String elementId;
	
	protected String rdfIdGson;
	
	public AbstractSEPAElement()
	{
		this.elementId = UUID.randomUUID().toString();	
		myId = new SupportsRdfIdImpl();
	}
	
	public AbstractSEPAElement(AbstractSEPAElement other)
	{
		this();
		this.elementId = other.getElementId();
		this.rdfIdGson = other.getRdfId().toString();
		System.out.println(this.getClass().getCanonicalName() +", " +other.getRdfId());
		this.setRdfId(other.getRdfId());
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
		return myId.getRdfId();
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void setRdfId(RdfKey arg0) {
		myId.setRdfId(arg0);	
	}

	public String getRdfIdGson() {
		return rdfIdGson;
	}

	public void setRdfIdGson(String rdfIdGson) {
		this.rdfIdGson = rdfIdGson;
	}
		
}
