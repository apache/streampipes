package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.MAPPING_PROPERTY_NARY)
@Entity
public class MappingPropertyNary extends MappingProperty {

	private static final long serialVersionUID = 7570213252902343160L;
	
	@RdfProperty(StreamPipes.MAPS_TO)
	private List<URI> mapsTo;
	
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

}
