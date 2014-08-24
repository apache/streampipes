package de.fzi.cep.sepa.model.impl.output;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:OutputStrategy")
@Entity
public abstract class OutputStrategy extends UnnamedSEPAElement {

	public OutputStrategy()
	{
		super();
	}
	
	
}
