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

	@RdfProperty("sepa:hasName")
	String name;
	
	public OutputStrategy()
	{
		super();
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
