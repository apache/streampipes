package org.streampipes.model.impl.eventproperty;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

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
