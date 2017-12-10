package org.streampipes.model.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.DATA_SINK_DESCRIPTION)
@Entity
public class DataSinkDescription extends ConsumableStreamPipesEntity {
	
	private static final long serialVersionUID = -6553066396392585731L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_EC_TYPE)
	private List<String> category;
	
	public DataSinkDescription(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.spDataStreams = new ArrayList<>();
		this.category = new ArrayList<>();
	}
	
	public DataSinkDescription(DataSinkDescription other)
	{
		super(other);
		this.category = new Cloner().ecTypes(other.getCategory());
	}
	
	public DataSinkDescription(String uri, String name, String description)
	{
		this(uri, name, description, "");
		this.category = new ArrayList<>();
	}
	
	public DataSinkDescription()
	{
		super();
		this.category = new ArrayList<>();
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	
}
