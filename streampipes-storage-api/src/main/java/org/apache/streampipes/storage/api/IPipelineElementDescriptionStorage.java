/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.storage.api;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface IPipelineElementDescriptionStorage {
	
	boolean storeInvocablePipelineElement(InvocableStreamPipesEntity element);

	boolean storeDataSource(DataSourceDescription sep);
	
	boolean storeDataSource(String jsonld);
	
	boolean storeDataProcessor(DataProcessorDescription sepa);
	
	boolean storeDataProcessor(String jsonld);
	
	DataSourceDescription getDataSourceById(URI rdfId);

	DataSourceDescription getDataSourceByAppId(String appId);

	DataSourceDescription getDataSourceById(String rdfId) throws URISyntaxException;
	
	DataProcessorDescription getDataProcessorById(String rdfId) throws URISyntaxException;
	
	DataProcessorDescription getDataProcessorById(URI rdfId);

	DataProcessorDescription getDataProcessorByAppId(String appId);
	
	DataSinkDescription getDataSinkById(String rdfId) throws URISyntaxException;
	
	DataSinkDescription getDataSinkById(URI rdfId);

	DataSinkDescription getDataSinkByAppId(String appId);
	
	List<DataSourceDescription> getAllDataSources();
	
	List<DataProcessorDescription> getAllDataProcessors();
	
	boolean deleteDataSource(DataSourceDescription sep);
	
	boolean deleteDataSource(String rdfId);
	
	boolean deleteDataProcessor(DataProcessorDescription sepa);
	
	boolean deleteDataProcessor(String rdfId);
	
	boolean exists(DataSourceDescription sep);
	
	boolean exists(DataProcessorDescription sepa);

	boolean existsDataProcessor(String elementId);

	boolean existsDataSource(String elementId);

	boolean existsDataSink(String elementId);
	
	boolean update(DataSourceDescription sep);
	
	boolean update(DataProcessorDescription sepa);

	boolean exists(DataSinkDescription sec);

	boolean update(DataSinkDescription sec);

	boolean deleteDataSink(DataSinkDescription sec);

	boolean deleteDataSink(String rdfId);
	
	boolean storeDataSink(DataSinkDescription sec);

	List<DataSinkDescription> getAllDataSinks();
	
	StaticProperty getStaticPropertyById(String rdfId);
	
	SpDataStream getEventStreamById(String rdfId);
	
}
