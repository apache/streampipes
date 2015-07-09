package de.fzi.cep.sepa.model.impl.staticproperty;

import java.net.URI;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:MappingPropertyUnary")
@Entity
public class MappingPropertyUnary extends MappingProperty{

	@RdfProperty("sepa:mapsTo")
	URI mapsTo;
	
	public MappingPropertyUnary()
	{
		super();
	}
	
	public MappingPropertyUnary(URI mapsFrom, String name, String description)
	{
		super(mapsFrom, name, description);	
	}
	
	public MappingPropertyUnary(String name, String description)
	{
		super(name, description);
	}

	public URI getMapsTo() {
		return mapsTo;
	}

	public void setMapsTo(URI mapsTo) {
		this.mapsTo = mapsTo;
	}
}
