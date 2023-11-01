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
package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.api.IDataStreamStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;

import java.util.List;
import java.util.NoSuchElementException;

public class PipelineElementDescriptionStorageImpl implements IPipelineElementDescriptionStorage {

  private final IDataProcessorStorage dataProcessorStorage;
  private final IDataStreamStorage dataStreamStorage;
  private final IDataSinkStorage dataSinkStorage;
  private final IAdapterStorage adapterStorage;

  public PipelineElementDescriptionStorageImpl() {
    this.dataProcessorStorage = new DataProcessorStorageImpl();
    this.dataStreamStorage = new DataStreamStorageImpl();
    this.dataSinkStorage = new DataSinkStorageImpl();
    this.adapterStorage = new AdapterDescriptionStorageImpl();
  }

  @Override
  public boolean storeInvocablePipelineElement(InvocableStreamPipesEntity element) {
    // TODO Check if this is needed
    return false;
  }

  @Override
  public boolean storeDataStream(SpDataStream stream) {
    this.dataStreamStorage.createElement(stream);
    return true;
  }

  @Override
  public boolean storeDataProcessor(DataProcessorDescription processorDescription) {
    this.dataProcessorStorage.createElement(processorDescription);
    return true;
  }

  @Override
  public SpDataStream getDataStreamByAppId(String appId) {
    return this.dataStreamStorage.getDataStreamByAppId(appId);
  }

  @Override
  public SpDataStream getDataStreamById(String rdfId) {
    return this.dataStreamStorage.getElementById(rdfId);
  }

  @Override
  public DataProcessorDescription getDataProcessorById(String rdfId) {
    return new DataProcessorStorageImpl().getElementById(rdfId);
  }

  @Override
  public DataProcessorDescription getDataProcessorByAppId(String appId) {
    return this.dataProcessorStorage.getFirstDataProcessorByAppId(appId);
  }

  @Override
  public DataSinkDescription getDataSinkById(String rdfId) {
    return this.dataSinkStorage.getElementById(rdfId);
  }

  @Override
  public DataSinkDescription getDataSinkByAppId(String appId) {
    return this.dataSinkStorage.getFirstDataSinkByAppId(appId);
  }

  @Override
  public AdapterDescription getAdapterById(String elementId) {
    return adapterStorage.getElementById(elementId);
  }

  @Override
  public AdapterDescription getAdapterByAppId(String appId) {
    return this.adapterStorage.getFirstAdapterByAppId(appId);
  }

  @Override
  public List<SpDataStream> getAllDataStreams() {
    return this.dataStreamStorage.getAll();
  }

  @Override
  public List<DataProcessorDescription> getAllDataProcessors() {
    return this.dataProcessorStorage.getAll();
  }

  @Override
  public List<AdapterDescription> getAllAdapterDescriptions() {
    return null;
  }

  @Override
  public boolean deleteDataStream(SpDataStream sep) {
    this.dataStreamStorage.deleteElement(sep);
    return true;
  }

  @Override
  public boolean deleteDataStream(String rdfId) {
    return deleteDataStream(getDataStreamById(rdfId));
  }

  @Override
  public boolean deleteDataProcessor(DataProcessorDescription processorDescription) {
    this.dataProcessorStorage.deleteElement(processorDescription);
    return true;
  }

  @Override
  public boolean deleteDataProcessor(String rdfId) {
    return deleteDataProcessor(getDataProcessorById(rdfId));
  }

  @Override
  public boolean deleteAdapterDescription(AdapterDescription adapterDescription) {
    adapterStorage.deleteAdapter(adapterDescription.getElementId());
    return true;
  }

  @Override
  public boolean deleteAdapterDescription(String elementId) {
    adapterStorage.deleteAdapter(elementId);
    return true;
  }

  @Override
  public boolean exists(SpDataStream stream) {
    return getEventStreamById(stream.getElementId()) != null;
  }

  @Override
  public boolean exists(AdapterDescription adapterDescription) {
    return existsAdapterDescription(adapterDescription.getElementId());
  }

  @Override
  public boolean exists(DataProcessorDescription processorDescription) {
    return getDataProcessorById(processorDescription.getElementId()) != null;
  }

  @Override
  public boolean existsDataProcessorByAppId(String appId) {
    try {
      getDataProcessorByAppId(appId);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @Override
  public boolean existsDataSinkByAppId(String appId) {
    try {
      getDataSinkByAppId(appId);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @Override
  public boolean existsDataProcessor(String elementId) {
    return getDataProcessorById(elementId) != null;
  }

  @Override
  public boolean existsDataStream(String elementId) {
    return getDataStreamById(elementId) != null;
  }

  @Override
  public boolean existsDataSink(String elementId) {
    return getDataSinkById(elementId) != null;
  }

  @Override
  public boolean existsAdapterDescription(String elementId) {
    return getAdapterById(elementId) != null;
  }

  @Override
  public boolean update(SpDataStream stream) {
    this.dataStreamStorage.updateElement(stream);
    return true;
  }

  @Override
  public boolean update(DataProcessorDescription processorDescription) {
    this.dataProcessorStorage.updateElement(processorDescription);
    return true;
  }

  @Override
  public boolean exists(DataSinkDescription sec) {
    return getDataSinkById(sec.getElementId()) != null;
  }

  @Override
  public boolean update(DataSinkDescription sec) {
    this.dataSinkStorage.updateElement(sec);
    return true;
  }

  @Override
  public boolean update(AdapterDescription adapter) {
    adapterStorage.updateElement(adapter);
    return true;
  }

  @Override
  public boolean deleteDataSink(DataSinkDescription sec) {
    this.dataSinkStorage.deleteElement(sec);
    return true;
  }

  @Override
  public boolean deleteDataSink(String rdfId) {
    return deleteDataSink(getDataSinkById(rdfId));
  }

  @Override
  public boolean storeDataSink(DataSinkDescription sec) {
    this.dataSinkStorage.createElement(sec);
    return true;
  }

  @Override
  public boolean storeAdapterDescription(AdapterDescription adapterDescription) {
    this.adapterStorage.storeAdapter(adapterDescription);
    return true;
  }

  @Override
  public List<DataSinkDescription> getAllDataSinks() {
    return this.dataSinkStorage.getAll();
  }

  @Override
  public SpDataStream getEventStreamById(String rdfId) {
    return dataStreamStorage.getElementById(rdfId);
  }
}
