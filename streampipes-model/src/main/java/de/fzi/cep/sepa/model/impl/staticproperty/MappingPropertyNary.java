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

	private static final long serialVersionUID = 7570213252902343160L;
	
	@RdfProperty("sepa:mapsTo")
	List<URI> mapsTo;
	
	public MappingPropertyNary()
	{
		super(StaticPropertyType.MappingPropertyNary);
		this.mapsTo = new ArrayList<>();
	}
	
	public MappingPropertyNary(MappingPropertyNary other) {
		super(other);
		this.mapsTo = other.getMapsTo();
	}
	
	public MappingPropertyNary(URI mapsFrom, String internalName, String label, String description)
	{
		super(StaticPropertyType.MappingPropertyNary, mapsFrom, internalName, label, description);
		this.mapsTo = new ArrayList<>();
	}
	
	public MappingPropertyNary(String internalName, String label, String description)
	{
		super(StaticPropertyType.MappingPropertyNary, internalName, label, description);
		this.mapsTo = new ArrayList<>();
	}

	public List<URI> getMapsTo() {
		return mapsTo;
	}

	public void setMapsTo(List<URI> mapsTo) {
		this.mapsTo = mapsTo;
	}
	
	public void accept(StaticPropertyVisitor visitor) {
		visitor.visit(this);
	}	
}
