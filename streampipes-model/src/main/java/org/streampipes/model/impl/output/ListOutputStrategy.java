package org.streampipes.model.impl.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("sepa:ListOutputStrategy")
@Entity
public class ListOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = -6400256021072543325L;
	
	@RdfProperty("sepa:listPropertyName")
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
