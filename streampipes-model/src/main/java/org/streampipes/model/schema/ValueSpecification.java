package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.VALUE_SPECIFICATION)
@MappedSuperclass
@Entity
public abstract class ValueSpecification extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 1L;
	
	public ValueSpecification() {
		super();
	}
	
	public ValueSpecification(ValueSpecification other) {
		super(other);
	}

}
