package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.OUTPUT_STRATEGY)
@MappedSuperclass
@Entity
public abstract class OutputStrategy extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 1953204905003864143L;
	
	@RdfProperty(StreamPipes.HAS_NAME)
	private String name;
	
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
