package org.streampipes.model.impl.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.impl.eventproperty.EventProperty;

import java.net.URI;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass("sepa:MappingProperty")
@MappedSuperclass
@Entity
public abstract class MappingProperty extends StaticProperty {

	private static final long serialVersionUID = -7849999126274124847L;
	
	@RdfProperty("sepa:mapsFrom")
	protected URI mapsFrom;

	private List<EventProperty> mapsFromOptions;
	
	protected MappingProperty()
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
}
