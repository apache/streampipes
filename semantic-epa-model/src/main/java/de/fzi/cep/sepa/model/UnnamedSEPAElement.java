package de.fzi.cep.sepa.model;

import com.clarkparsia.empire.annotation.RdfId;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 *
 */
public abstract class UnnamedSEPAElement extends AbstractSEPAElement {
	
	@RdfId
	private String elementName;
	
	public UnnamedSEPAElement()
	{
		super();
		this.elementName = super.getElementId();
	}

	public String getElementName() {
	
		return super.getElementId();
	}

	public void setElementName(String elementName) {
		super.setElementId(elementName);
	}

}
