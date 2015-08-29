package de.fzi.cep.sepa.model;

import com.clarkparsia.empire.annotation.RdfId;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 *
 */
public abstract class UnnamedSEPAElement extends AbstractSEPAElement {
	
	private static final long serialVersionUID = 8051137255998890188L;
	
	@RdfId
	protected String elementName;
	
	public UnnamedSEPAElement()
	{
		super();
		this.elementName = super.getElementId();
	}
	
	public UnnamedSEPAElement(UnnamedSEPAElement other)
	{
		super(other);
		this.elementName = other.getElementName();
	}
	
	public UnnamedSEPAElement(String elementName)
	{
		super();
		super.setElementId(elementName);
		this.elementName = super.getElementId();
	}

	public String getElementName() {
	
		return super.getElementId();
	}

	public void setElementName(String elementName) {
		super.setElementId(elementName);
	}

}
