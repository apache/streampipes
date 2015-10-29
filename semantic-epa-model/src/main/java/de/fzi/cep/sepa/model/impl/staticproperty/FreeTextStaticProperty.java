package de.fzi.cep.sepa.model.impl.staticproperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:FreeTextStaticProperty")
@Entity
public class FreeTextStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 5029422126802713205L;

	@RdfProperty("sepa:hasValue")
	protected String value;
	
	@RdfProperty("sepa:hasType")
	protected String requiredDomainProperty;
	
	@OneToOne(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasValueSpecification")
	protected PropertyValueSpecification valueSpecification;
	
	public FreeTextStaticProperty() {
		super();
	}
	
	public FreeTextStaticProperty(FreeTextStaticProperty other) {
		super(other);
		this.requiredDomainProperty = other.getRequiredDomainProperty();
		if (other.getValueSpecification() != null) this.valueSpecification = new PropertyValueSpecification(other.getValueSpecification());
		this.value = other.getValue();
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description)
	{
		super(internalName, label, description);
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description, String type)
	{
		super(internalName, label, description);
		this.requiredDomainProperty = type;
	}
	
	public FreeTextStaticProperty(String internalName, String label, String description, PropertyValueSpecification valueSpecification)
	{
		super(internalName, label, description);
		this.valueSpecification = valueSpecification;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRequiredDomainProperty() {
		return requiredDomainProperty;
	}

	public void setRequiredDomainProperty(String type) {
		this.requiredDomainProperty = type;
	}
	
	public PropertyValueSpecification getValueSpecification() {
		return valueSpecification;
	}

	public void setValueSpecification(PropertyValueSpecification valueSpecification) {
		this.valueSpecification = valueSpecification;
	}
		
	
	public void accept(StaticPropertyVisitor visitor) {
		visitor.visit(this);
	}	
	
}
