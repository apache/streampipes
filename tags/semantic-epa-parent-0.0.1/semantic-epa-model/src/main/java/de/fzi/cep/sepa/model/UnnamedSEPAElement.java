package de.fzi.cep.sepa.model;


import org.apache.commons.lang.RandomStringUtils;

import com.clarkparsia.empire.annotation.RdfId;
import com.clarkparsia.empire.annotation.RdfProperty;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 *
 */
public abstract class UnnamedSEPAElement extends AbstractSEPAElement {
	
	private static final long serialVersionUID = 8051137255998890188L;
	
	private static final String prefix = "urn:fzi.de:";
	
	@RdfId
	@RdfProperty("sepa:elementName")
	protected String elementName;
	
	public UnnamedSEPAElement()
	{
		super();
		this.elementName = prefix + this.getClass().getSimpleName().toLowerCase() +":" +RandomStringUtils.randomAlphabetic(6);
	}
	
	public UnnamedSEPAElement(UnnamedSEPAElement other)
	{
		super(other);
		this.elementName = other.getElementName();
	}
	
	public UnnamedSEPAElement(String elementName)
	{
		super();
		this.elementName = elementName;
	}

	public String getElementName() {
	
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	public String getElementId()
	{
		return elementName;
	}

}
