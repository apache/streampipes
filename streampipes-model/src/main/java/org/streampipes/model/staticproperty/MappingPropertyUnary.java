package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.MAPPING_PROPERTY_UNARY)
@Entity
public class MappingPropertyUnary extends MappingProperty{

	private static final long serialVersionUID = 2903529966128844426L;
	
	@RdfProperty(StreamPipes.MAPS_TO)
	private URI mapsTo;
	
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

}
