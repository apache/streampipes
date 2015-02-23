package de.fzi.cep.sepa.model.impl;

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

	@RdfProperty("sepa:mapsFrom")
	URI mapsFrom;
	
	
	protected MappingProperty()
	{
		super();
	}
	
	protected MappingProperty(URI mapsFrom, String name, String description)
	{
		super(name, description);
		this.mapsFrom = mapsFrom;
	}
	
	protected MappingProperty(String name, String description)
	{
		super(name, description);
	}

	public URI getMapsFrom() {
		return mapsFrom;
	}

	public void setMapsFrom(URI mapsFrom) {
		this.mapsFrom = mapsFrom;
	}	
}
