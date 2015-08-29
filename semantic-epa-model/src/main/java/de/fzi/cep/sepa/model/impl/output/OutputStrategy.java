package de.fzi.cep.sepa.model.impl.output;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
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
