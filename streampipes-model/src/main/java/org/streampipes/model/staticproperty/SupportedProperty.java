package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.SUPPORTED_PROPERTY)
@Entity
public class SupportedProperty extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 1L;

	@RdfProperty(StreamPipes.REQUIRES_PROPERTY)
	private String propertyId;
	
	@RdfProperty(SO.ValueRequired)
	private boolean valueRequired;
	
	@RdfProperty(SO.Value)
	private String value;
	
	public SupportedProperty(SupportedProperty other)
	{
		super();
		this.propertyId = other.getPropertyId();
		this.valueRequired = other.isValueRequired();
		this.value = other.getValue();
	}
	
	public SupportedProperty()
	{
		super();
	}
	
	public SupportedProperty(String propertyId, boolean valueRequired)
	{
		this();
		this.propertyId = propertyId;
		this.valueRequired = valueRequired;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public boolean isValueRequired() {
		return valueRequired;
	}

	public void setValueRequired(boolean valueRequired) {
		this.valueRequired = valueRequired;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
