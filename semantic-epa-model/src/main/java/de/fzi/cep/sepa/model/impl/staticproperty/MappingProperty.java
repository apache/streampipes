package de.fzi.cep.sepa.model.impl.staticproperty;

import java.net.URI;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:MappingProperty")
@MappedSuperclass
@Entity
public abstract class MappingProperty extends StaticProperty {

	private static final long serialVersionUID = -7849999126274124847L;
	
	@RdfProperty("sepa:mapsFrom")
	protected URI mapsFrom;
	
	protected MappingProperty()
	{
		super();
	}
	
	public MappingProperty(MappingProperty other)
	{
		super(other);
		this.mapsFrom = other.getMapsFrom();
	}
	
	protected MappingProperty(URI mapsFrom, String internalName, String label, String description)
	{
		super(internalName, label, description);
		this.mapsFrom = mapsFrom;
	}
	
	protected MappingProperty(String internalName, String label, String description)
	{
		super(internalName, label, description);
	}

	public URI getMapsFrom() {
		return mapsFrom;
	}

	public void setMapsFrom(URI mapsFrom) {
		this.mapsFrom = mapsFrom;
	}	
}
