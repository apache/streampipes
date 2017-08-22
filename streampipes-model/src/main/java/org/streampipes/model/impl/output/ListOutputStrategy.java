package org.streampipes.model.impl.output;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:ListOutputStrategy")
@Entity
public class ListOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = -6400256021072543325L;
	
	@RdfProperty("sepa:listPropertyName")
	String propertyName;
	
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
