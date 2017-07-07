package org.streampipes.model.impl.staticproperty;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import org.streampipes.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "so", "http://schema.org/"})
@RdfsClass("sepa:SupportedProperty")
@Entity
public class SupportedProperty extends UnnamedSEPAElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@RdfProperty("sepa:requiresProperty")
	private String propertyId;
	
	@RdfProperty("so:valueRequired")
	private boolean valueRequired;
	
	@RdfProperty("so:value")
	private String value;
	
	public SupportedProperty(SupportedProperty other)
	{
		super();
		this.propertyId = other.getPropertyId();
		this.valueRequired = other.isValueRequired();
		this.value = other.getValue();
	}
	
	public SupportedProperty()
	{
		super();
	}
	
	public SupportedProperty(String propertyId, boolean valueRequired)
	{
		this();
		this.propertyId = propertyId;
		this.valueRequired = valueRequired;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public boolean isValueRequired() {
		return valueRequired;
	}

	public void setValueRequired(boolean valueRequired) {
		this.valueRequired = valueRequired;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
