package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.MAPPING_PROPERTY)
@MappedSuperclass
@Entity
public abstract class MappingProperty extends StaticProperty {

	private static final long serialVersionUID = -7849999126274124847L;
	
	@RdfProperty(StreamPipes.MAPS_FROM)
	protected URI mapsFrom;

	private List<EventProperty> mapsFromOptions;

	@OneToOne(fetch = FetchType.EAGER,
					cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_PROPERTY_SCOPE)
	private String propertyScope;
	
	public MappingProperty()
	{
		super();
	}

	public MappingProperty(StaticPropertyType type) {
		super(type);
	}
	
	public MappingProperty(MappingProperty other)
	{
		super(other);
		this.mapsFrom = other.getMapsFrom();
		this.propertyScope = other.getPropertyScope();
        //this.mapsFromOptions = other.getMapsFromOptions();
	}
	
	protected MappingProperty(StaticPropertyType type, URI mapsFrom, String internalName, String label, String description)
	{
		super(type, internalName, label, description);
		this.mapsFrom = mapsFrom;
	}
	
	protected MappingProperty(StaticPropertyType type, String internalName, String label, String description)
	{
		super(type, internalName, label, description);
	}

	public URI getMapsFrom() {
		return mapsFrom;
	}

	public void setMapsFrom(URI mapsFrom) {
		this.mapsFrom = mapsFrom;
	}

	public List<EventProperty> getMapsFromOptions() {
		return mapsFromOptions;
	}

	public void setMapsFromOptions(List<EventProperty> mapsFromOptions) {
		this.mapsFromOptions = mapsFromOptions;
	}

	public String getPropertyScope() {
		return propertyScope;
	}

	public void setPropertyScope(String propertyScope) {
		this.propertyScope = propertyScope;
	}
}
