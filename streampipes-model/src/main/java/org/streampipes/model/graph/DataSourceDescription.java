package org.streampipes.model.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.DataSource;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * class that represents Semantic Event Producers.
 *
 */
@RdfsClass(StreamPipes.DATA_SOURCE_DESCRIPTION)
@Entity
public class DataSourceDescription extends NamedStreamPipesEntity {
	
	private static final long serialVersionUID = 5607030219013954697L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.PRODUCES)
	private List<SpDataStream> spDataStreams;
	
	private DataSource dataSource;
		
	public DataSourceDescription() {
		super();
		spDataStreams = new ArrayList<>();
	}
	
	public DataSourceDescription(DataSourceDescription other)
	{
		super(other);
		this.spDataStreams = new Cloner().streams(other.getSpDataStreams());
		this.spDataStreams.forEach(e -> e.setCategory(Arrays.asList(this.getElementId())));
	}
	
	public DataSourceDescription(String uri, String name, String description, String iconUrl, List<SpDataStream> spDataStreams)
	{
		super(uri, name, description, iconUrl);
		this.spDataStreams = spDataStreams;
	}
	
	public DataSourceDescription(String uri, String name2, String description2, String iconUrl) {
		this(uri, name2, description2, iconUrl, new ArrayList<SpDataStream>());
	}
	
	public DataSourceDescription(String uri, String name, String description) {
		this(uri, name, description, "", new ArrayList<SpDataStream>());
	}

	public List<SpDataStream> getSpDataStreams() {
		return spDataStreams;
	}

	public void setSpDataStreams(List<SpDataStream> spDataStreams) {
		this.spDataStreams = spDataStreams;
	}
	
	public void addEventStream(SpDataStream spDataStream)
	{
		spDataStreams.add(spDataStream);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}			
}
