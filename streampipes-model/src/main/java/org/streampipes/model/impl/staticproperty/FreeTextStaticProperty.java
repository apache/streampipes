package org.streampipes.model.impl.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass("sepa:FreeTextStaticProperty")
@Entity
public class FreeTextStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 1L;

	@RdfProperty("sepa:hasValue")
	private String value;
	
	@RdfProperty("sepa:requiresDatatype")
	private URI requiredDatatype;
	
	@RdfProperty("sepa:requiresDomainProperty")
	private URI requiredDomainProperty;
	
	@RdfProperty("sepa:mapsTo")
	private URI mapsTo;

	@RdfProperty("sepa:multiLine")
	private boolean multiLine;

	@RdfProperty("sepa:htmlAllowed")
	private boolean htmlAllowed;

	@RdfProperty("sepa:placeholdersSupported")
	private boolean placeholdersSupported;
	
	@OneToOne(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasValueSpecification")
	private PropertyValueSpecification valueSpecification;
	
	public FreeTextStaticProperty() {
		super(StaticPropertyType.FreeTextStaticProperty);
	}
	
	public FreeTextStaticProperty(FreeTextStaticProperty other) {
		super(other);
		this.requiredDomainProperty = other.getRequiredDomainProperty();
		this.requiredDatatype = other.getRequiredDatatype();
		if (other.getValueSpecification() != null) this.valueSpecification = new PropertyValueSpecification(other.getValueSpecification());
		this.value = other.getValue();
		this.htmlAllowed = other.isHtmlAllowed();
		this.multiLine = other.isMultiLine();
		this.placeholdersSupported = other.isPlaceholdersSupported();
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description)
	{
		super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description, URI type)
	{
		super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
		this.requiredDomainProperty = type;
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description, URI type, URI mapsTo)
	{
		super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
		this.mapsTo = mapsTo;
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description, PropertyValueSpecification valueSpecification)
	{
		super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
		this.valueSpecification = valueSpecification;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public URI getRequiredDomainProperty() {
		return requiredDomainProperty;
	}

	public void setRequiredDomainProperty(URI type) {
		this.requiredDomainProperty = type;
	}
	
	public PropertyValueSpecification getValueSpecification() {
		return valueSpecification;
	}

	public void setValueSpecification(PropertyValueSpecification valueSpecification) {
		this.valueSpecification = valueSpecification;
	}
		
	
	public URI getRequiredDatatype() {
		return requiredDatatype;
	}

	public void setRequiredDatatype(URI requiredDatatype) {
		this.requiredDatatype = requiredDatatype;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public boolean isHtmlAllowed() {
		return htmlAllowed;
	}

	public void setHtmlAllowed(boolean htmlAllowed) {
		this.htmlAllowed = htmlAllowed;
	}

	public boolean isPlaceholdersSupported() {
		return placeholdersSupported;
	}

	public void setPlaceholdersSupported(boolean placeholdersSupported) {
		this.placeholdersSupported = placeholdersSupported;
	}

	public URI getMapsTo() {
		return mapsTo;
	}

	public void setMapsTo(URI mapsTo) {
		this.mapsTo = mapsTo;
	}
}
