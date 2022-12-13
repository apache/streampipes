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
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.net.URI;
import java.util.List;

public interface IPipelineElementDescriptionStorage {

  boolean storeInvocablePipelineElement(InvocableStreamPipesEntity element);

  boolean storeDataStream(SpDataStream stream);

  boolean storeDataStream(String jsonld);

  boolean storeDataProcessor(DataProcessorDescription processorDescription);

  boolean storeDataProcessor(String jsonld);

  SpDataStream getDataStreamById(URI rdfId);

  SpDataStream getDataStreamByAppId(String appId);

  SpDataStream getDataStreamById(String rdfId);

  DataProcessorDescription getDataProcessorById(String rdfId);

  DataProcessorDescription getDataProcessorById(URI rdfId);

  DataProcessorDescription getDataProcessorByAppId(String appId);

  DataSinkDescription getDataSinkById(String rdfId);

  DataSinkDescription getDataSinkById(URI rdfId);

  DataSinkDescription getDataSinkByAppId(String appId);

  List<SpDataStream> getAllDataStreams();

  List<DataProcessorDescription> getAllDataProcessors();

  boolean deleteDataStream(SpDataStream sep);

  boolean deleteDataStream(String rdfId);

  boolean deleteDataProcessor(DataProcessorDescription processorDescription);

  boolean deleteDataProcessor(String rdfId);

  boolean exists(SpDataStream stream);

  boolean exists(DataProcessorDescription processorDescription);

  boolean existsDataProcessorByAppId(String appId);

  boolean existsDataSinkByAppId(String appId);

  boolean existsDataProcessor(String elementId);

  boolean existsDataStream(String elementId);

  boolean existsDataSink(String elementId);

  boolean update(SpDataStream stream);

  boolean update(DataProcessorDescription processorDescription);

  boolean exists(DataSinkDescription sec);

  boolean update(DataSinkDescription sec);

  boolean deleteDataSink(DataSinkDescription sec);

  boolean deleteDataSink(String rdfId);

  boolean storeDataSink(DataSinkDescription sec);

  List<DataSinkDescription> getAllDataSinks();

  StaticProperty getStaticPropertyById(String rdfId);

  SpDataStream getEventStreamById(String rdfId);

}
