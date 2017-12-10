package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.RDFS;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.STATIC_PROPERTY)
@MappedSuperclass
@Entity
public abstract class StaticProperty extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 2509153122084646025L;

	@RdfProperty(RDFS.LABEL)
	private String label;
	
	@RdfProperty(RDFS.DESCRIPTION)
	private String description;
	
	@RdfProperty(StreamPipes.INTERNAL_NAME)
	private String internalName;
	
	@RdfProperty(SO.ValueRequired)
	protected boolean valueRequired;

	protected StaticPropertyType staticPropertyType;
	
	
	public StaticProperty()
	{
		super();
	}

	public StaticProperty(StaticPropertyType type) {
		super();
		this.staticPropertyType = type;
	}
	
	public StaticProperty(StaticProperty other)
	{
		super(other);
		this.description = other.getDescription();
		this.internalName = other.getInternalName();
		this.valueRequired = other.isValueRequired();
		this.staticPropertyType = other.getStaticPropertyType();
		this.label = other.getLabel();
	}
	
	public StaticProperty(StaticPropertyType type, String internalName, String label, String description)
	{
		super();
		this.staticPropertyType = type;
		this.internalName = internalName;
		this.label = label;
		this.description = description;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String name) {
		this.internalName = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isValueRequired() {
		return valueRequired;
	}

	public void setValueRequired(boolean valueRequired) {
		this.valueRequired = valueRequired;
	}

	public StaticPropertyType getStaticPropertyType() {
		return staticPropertyType;
	}

	public void setStaticPropertyType(StaticPropertyType staticPropertyType) {
		this.staticPropertyType = staticPropertyType;
	}
}
