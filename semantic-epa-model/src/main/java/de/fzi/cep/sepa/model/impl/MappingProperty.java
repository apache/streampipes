package de.fzi.cep.sepa.model.impl;

import java.net.URI;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:MappingProperty")
@Entity
public class MappingProperty extends StaticProperty {

	@RdfProperty("sepa:mapsTo")
	URI mapsTo;
	
	public MappingProperty()
	{
		super();
	}
	
	public MappingProperty(String name, String description)
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
