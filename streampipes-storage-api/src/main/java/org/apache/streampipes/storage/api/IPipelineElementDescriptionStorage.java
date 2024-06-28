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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;

import java.util.List;

public interface IPipelineElementDescriptionStorage {

  boolean storeDataStream(SpDataStream stream);

  boolean storeDataProcessor(DataProcessorDescription processorDescription);

  SpDataStream getDataStreamById(String rdfId);

  DataProcessorDescription getDataProcessorById(String rdfId);

  DataProcessorDescription getDataProcessorByAppId(String appId);

  DataSinkDescription getDataSinkById(String rdfId);

  DataSinkDescription getDataSinkByAppId(String appId);

  AdapterDescription getAdapterById(String elementId);

  List<SpDataStream> getAllDataStreams();

  List<DataProcessorDescription> getAllDataProcessors();

  boolean deleteDataStream(SpDataStream sep);

  boolean deleteDataProcessor(DataProcessorDescription processorDescription);

  boolean exists(SpDataStream stream);

  boolean exists(AdapterDescription adapterDescription);

  boolean exists(DataProcessorDescription processorDescription);

  boolean existsDataProcessor(String elementId);

  boolean existsDataStream(String elementId);

  boolean existsDataSink(String elementId);

  boolean existsAdapterDescription(String elementId);

  boolean update(SpDataStream stream);

  boolean update(DataProcessorDescription processorDescription);

  boolean exists(DataSinkDescription sec);

  boolean update(DataSinkDescription sec);

  boolean update(AdapterDescription adapter);

  boolean deleteDataSink(DataSinkDescription sec);

  boolean storeDataSink(DataSinkDescription sec);

  boolean storeAdapterDescription(AdapterDescription adapterDescription);

  List<DataSinkDescription> getAllDataSinks();

  SpDataStream getEventStreamById(String rdfId);

}
