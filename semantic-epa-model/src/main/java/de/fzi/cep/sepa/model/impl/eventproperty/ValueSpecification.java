package de.fzi.cep.sepa.model.impl.eventproperty;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("sepa:ValueSpecification")
@MappedSuperclass
@Entity
public abstract class ValueSpecification extends UnnamedSEPAElement {

	private static final long serialVersionUID = 1L;
	
	public ValueSpecification() {
		super();
	}
	
	public ValueSpecification(ValueSpecification other) {
		super(other);
	}

}
