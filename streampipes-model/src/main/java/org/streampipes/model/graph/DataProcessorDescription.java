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
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.DATA_PROCESSOR_DESCRIPTION)
@Entity
public class DataProcessorDescription extends ConsumableStreamPipesEntity {

	private static final long serialVersionUID = 3995767921861518597L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_OUTPUT_STRATEGY)
	private List<OutputStrategy> outputStrategies;
	
	private String pathName;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_EPA_TYPE)
	private List<String> category;

	public DataProcessorDescription(DataProcessorDescription other)
	{
		super(other);
		this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
		this.pathName = other.getPathName();
		this.category = new Cloner().epaTypes(other.getCategory());
	}
	
	public DataProcessorDescription()
	{
		super();
		this.outputStrategies = new ArrayList<>();
		this.category = new ArrayList<>();
	}
	
	public DataProcessorDescription(String uri, String name, String description, String iconUrl, List<SpDataStream> spDataStreams, List<StaticProperty> staticProperties, List<OutputStrategy> outputStrategies)
	{
		super(uri, name, description, iconUrl);
		this.pathName = uri;
		this.spDataStreams = spDataStreams;
		this.staticProperties = staticProperties;
		this.outputStrategies = outputStrategies;
	}
	
	public DataProcessorDescription(String pathName, String name, String description, String iconUrl)
	{
		super(pathName, name, description, iconUrl);
		this.pathName = pathName;
		spDataStreams = new ArrayList<>();
		staticProperties = new ArrayList<>();
	}
	
	public DataProcessorDescription(String pathName, String name, String description)
	{
		super(pathName, name, description, "");
		this.pathName = pathName;
		spDataStreams = new ArrayList<>();
		staticProperties = new ArrayList<>();
	}

	

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}

}
