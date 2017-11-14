package org.streampipes.model;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.DATA_SOURCE)
@Entity
public class DataSource extends NamedStreamPipesEntity {
	
	private static final long serialVersionUID = -6144439857250547201L;

	public DataSource()
	{
		super();
	}
	
	public DataSource(String uri, String name, String description, String iconUrl) {
		super(uri, name, description, iconUrl);
		// TODO Auto-generated constructor stub
	}

	

	

	
}
