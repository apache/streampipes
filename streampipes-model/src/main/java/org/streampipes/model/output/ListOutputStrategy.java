package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.LIST_OUTPUT_STRATEGY)
@Entity
public class ListOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = -6400256021072543325L;
	
	@RdfProperty(StreamPipes.LIST_PROPERTY_NAME)
	private String propertyName;
	
	public ListOutputStrategy() {
		super();
	}
	
	public ListOutputStrategy(ListOutputStrategy other) {
		super(other);
		this.propertyName = other.getPropertyName();
	}

	public  ListOutputStrategy(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	
}
