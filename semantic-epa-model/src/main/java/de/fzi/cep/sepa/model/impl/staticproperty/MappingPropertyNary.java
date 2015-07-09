package de.fzi.cep.sepa.model.impl.staticproperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:MappingPropertyNary")
@Entity
public class MappingPropertyNary extends MappingProperty {

	@RdfProperty("sepa:mapsTo")
	List<URI> mapsTo;
	
	public MappingPropertyNary()
	{
		super();
		this.mapsTo = new ArrayList<>();
	}
	
	public MappingPropertyNary(URI mapsFrom, String name, String description)
	{
		super(mapsFrom, name, description);	
		this.mapsTo = new ArrayList<>();
	}
	
	public MappingPropertyNary(String name, String description)
	{
		super(name, description);
		this.mapsTo = new ArrayList<>();
	}

	public List<URI> getMapsTo() {
		return mapsTo;
	}

	public void setMapsTo(List<URI> mapsTo) {
		this.mapsTo = mapsTo;
	}
}
