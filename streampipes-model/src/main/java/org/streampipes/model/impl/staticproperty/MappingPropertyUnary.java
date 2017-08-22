package org.streampipes.model.impl.staticproperty;

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

	private static final long serialVersionUID = 2903529966128844426L;
	
	@RdfProperty("sepa:mapsTo")
	URI mapsTo;
	
	public MappingPropertyUnary()
	{
		super(StaticPropertyType.MappingPropertyUnary);
	}
	
	public MappingPropertyUnary(MappingPropertyUnary other)
	{
		super(other);
		this.mapsTo = other.getMapsTo();
	}
	
	public MappingPropertyUnary(URI mapsFrom, String internalName, String label, String description)
	{
		super(StaticPropertyType.MappingPropertyUnary, mapsFrom, internalName, label, description);
	}
	
	public MappingPropertyUnary(String internalName, String label, String description)
	{
		super(StaticPropertyType.MappingPropertyUnary, internalName, label, description);
	}

	public URI getMapsTo() {
		return mapsTo;
	}

	public void setMapsTo(URI mapsTo) {
		this.mapsTo = mapsTo;
	}
	
	public void accept(StaticPropertyVisitor visitor) {
		visitor.visit(this);
	}	
}
