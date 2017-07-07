package de.fzi.cep.sepa.model.impl.staticproperty;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:FreeTextStaticProperty")
@Entity
public class FreeTextStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 1L;

	@RdfProperty("sepa:hasValue")
	protected String value;
	
	@RdfProperty("sepa:requiresDatatype")
	protected URI requiredDatatype; 
	
	@RdfProperty("sepa:requiresDomainProperty")
	protected URI requiredDomainProperty;
	
	@RdfProperty("sepa:mapsTo")
	protected URI mapsTo;

	@RdfProperty("sepa:multiLine")
	protected boolean multiLine;

	@RdfProperty("sepa:htmlAllowed")
	protected boolean htmlAllowed;

	@RdfProperty("sepa:placeholdersSupported")
	protected boolean placeholdersSupported;
	
	@OneToOne(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasValueSpecification")
	protected PropertyValueSpecification valueSpecification;
	
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
}
