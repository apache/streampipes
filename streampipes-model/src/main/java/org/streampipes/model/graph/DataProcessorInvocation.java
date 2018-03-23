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
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.DATA_PROCESSOR_INVOCATION)
@Entity
public class DataProcessorInvocation extends InvocableStreamPipesEntity implements Serializable {

	private static final long serialVersionUID = 865870355944824186L;

	@OneToOne (fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty(StreamPipes.PRODUCES)
  private SpDataStream outputStream;
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_OUTPUT_STRATEGY)
	private List<OutputStrategy> outputStrategies;
	
	private String pathName;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_EPA_TYPE)
	private List<String> category;
	
	public DataProcessorInvocation(DataProcessorDescription sepa)
	{
		super();
		this.setName(sepa.getName());
		this.setDescription(sepa.getDescription());
		this.setIconUrl(sepa.getIconUrl());
		this.setInputStreams(sepa.getSpDataStreams());
		this.setSupportedGrounding(sepa.getSupportedGrounding());
		this.setStaticProperties(sepa.getStaticProperties());
		this.setOutputStrategies(sepa.getOutputStrategies());
		this.setBelongsTo(sepa.getElementId().toString());
		this.category = sepa.getCategory();
		this.setStreamRequirements(sepa.getSpDataStreams());
		//this.setUri(belongsTo +"/" +getElementId());		
	}
	
	public DataProcessorInvocation(DataProcessorInvocation other)
	{
		super(other);
		this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
		if (other.getOutputStream() != null) this.outputStream =  new Cloner().stream(other.getOutputStream());
		this.pathName = other.getPathName();
		this.category = new Cloner().epaTypes(other.getCategory());
	}

	public DataProcessorInvocation(DataProcessorDescription sepa, String domId)
	{
		this(sepa);
		this.DOM = domId;
	}
	
	public DataProcessorInvocation()
	{
		super();
		inputStreams = new ArrayList<SpDataStream>();
	}
	
	public DataProcessorInvocation(String uri, String name, String description, String iconUrl, String pathName, List<SpDataStream> spDataStreams, List<StaticProperty> staticProperties)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		this.inputStreams = spDataStreams;
		this.staticProperties = staticProperties;
	}
	
	public DataProcessorInvocation(String uri, String name, String description, String iconUrl, String pathName)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		inputStreams = new ArrayList<SpDataStream>();
		staticProperties = new ArrayList<StaticProperty>();
	}
	
	public boolean addInputStream(SpDataStream spDataStream)
	{
		return inputStreams.add(spDataStream);
	}
	
	

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public SpDataStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(SpDataStream outputStream) {
		this.outputStream = outputStream;
	}

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}
	
}
