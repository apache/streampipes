package org.streampipes.model.impl.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass("sepa:OutputStrategy")
@MappedSuperclass
@Entity
public abstract class OutputStrategy extends UnnamedSEPAElement {

	private static final long serialVersionUID = 1953204905003864143L;
	
	@RdfProperty("sepa:hasName")
	protected String name;
	
	public OutputStrategy()
	{
		super();
	}
	
	public OutputStrategy(OutputStrategy other) {
		super(other);
		this.name = other.getName();
	}
	
	public OutputStrategy(String name)
	{
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
