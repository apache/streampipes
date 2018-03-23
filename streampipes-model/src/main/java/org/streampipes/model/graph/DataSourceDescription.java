/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.DataSource;
import org.streampipes.model.SpDataSequence;
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
	private List<SpDataSequence> spDataStreams;
	
	private DataSource dataSource;
		
	public DataSourceDescription() {
		super();
		spDataStreams = new ArrayList<>();
	}
	
	public DataSourceDescription(DataSourceDescription other)
	{
		super(other);
		this.spDataStreams = new Cloner().seq(other.getSpDataStreams());
		this.spDataStreams.forEach(e -> e.setCategory(Arrays.asList(this.getElementId())));
	}
	
	public DataSourceDescription(String uri, String name, String description, String iconUrl, List<SpDataSequence> spDataStreams)
	{
		super(uri, name, description, iconUrl);
		this.spDataStreams = spDataStreams;
	}
	
	public DataSourceDescription(String uri, String name2, String description2, String iconUrl) {
		this(uri, name2, description2, iconUrl, new ArrayList<>());
	}
	
	public DataSourceDescription(String uri, String name, String description) {
		this(uri, name, description, "", new ArrayList<>());
	}

	public List<SpDataSequence> getSpDataStreams() {
		return spDataStreams;
	}

	public void setSpDataStreams(List<SpDataSequence> spDataStreams) {
		this.spDataStreams = spDataStreams;
	}
	
	public void addEventStream(SpDataSequence spDataStream)
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
