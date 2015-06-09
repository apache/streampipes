package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:FreeTextStaticProperty")
@Entity
public class FreeTextStaticProperty extends StaticProperty {

	@RdfProperty("sepa:hasValue")
	String value;
	
	@RdfProperty("sepa:hasType")
	String type;
	
	public FreeTextStaticProperty() {
		super();
	}
	
	public FreeTextStaticProperty(String name, String description)
	{
		super(name, description);
	}
	
	public FreeTextStaticProperty(String name, String description, String type)
	{
		super(name, description);
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
