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

package org.apache.streampipes.storage.rdf4j.impl;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class PipelineElementInMemoryStorage implements IPipelineElementDescriptionStorageCache {

  private Map<String, DataSinkDescription> inMemoryDataSinkStorage;
  private Map<String, DataProcessorDescription> inMemoryDataProcessorStorage;
  private Map<String, SpDataStream> inMemoryDataStreamStorage;
  private IPipelineElementDescriptionStorage sesameStorage;


  public PipelineElementInMemoryStorage(IPipelineElementDescriptionStorage sesameStorage) {
    this.inMemoryDataSinkStorage = new HashMap<>();
    this.inMemoryDataProcessorStorage = new HashMap<>();
    this.inMemoryDataStreamStorage = new HashMap<>();
    this.sesameStorage = sesameStorage;
    init();
  }

  private void init() {
    initializeDataSinkStorage();
    initializeDataProcessorStorage();
    initializeDataStreamStorage();
  }

  private void initializeDataSinkStorage() {
    inMemoryDataSinkStorage.clear();
    List<DataSinkDescription> secs = sort(sesameStorage
            .getAllDataSinks()
            .stream()
            .map(DataSinkDescription::new)
            .collect(Collectors.toList()));
    secs.forEach(sec -> inMemoryDataSinkStorage.put(sec.getElementId(), sec));
  }

  private void initializeDataProcessorStorage() {
    inMemoryDataProcessorStorage.clear();
    List<DataProcessorDescription> sepas = sort(sesameStorage
            .getAllDataProcessors()
            .stream()
            .map(DataProcessorDescription::new)
            .collect(Collectors.toList()));
    sepas.forEach(sepa -> inMemoryDataProcessorStorage.put(sepa.getElementId(), sepa));
  }

  private void initializeDataStreamStorage() {
    inMemoryDataStreamStorage.clear();
    List<SpDataStream> streams = sesameStorage.getAllDataStreams();
    streams.forEach(stream ->
                    stream.getEventSchema()
                            .getEventProperties()
                            .sort(Comparator.comparingInt(EventProperty::getIndex)));
    streams.forEach(stream -> inMemoryDataStreamStorage.put(stream.getElementId(), stream));
  }

  private <T extends ConsumableStreamPipesEntity> List<T> sort(List<T> processingElements) {
    processingElements.forEach(pe -> {
      pe.getStaticProperties().sort(Comparator.comparingInt(StaticProperty::getIndex));
      pe.getSpDataStreams().sort(Comparator.comparingInt(SpDataStream::getIndex));
    });
    return processingElements;
  }

  @Override
  public boolean storeInvocablePipelineElement(InvocableStreamPipesEntity element) {
    return sesameStorage.storeInvocablePipelineElement(element);
  }

  @Override
  public boolean storeDataStream(SpDataStream stream) {
    return sesameStorage.storeDataStream(stream);
  }

  @Override
  public boolean storeDataStream(String jsonld) {
    return sesameStorage.storeDataStream(jsonld);
  }

  @Override
  public boolean storeDataProcessor(DataProcessorDescription sepa) {
    return sesameStorage.storeDataProcessor(sepa);
  }

  @Override
  public boolean storeDataProcessor(String jsonld) {
    return sesameStorage.storeDataProcessor(jsonld);
  }

  @Override
  public SpDataStream getDataStreamById(URI rdfId) {
    return new Cloner().mapSequence(inMemoryDataStreamStorage.get(rdfId.toString()));
  }

  @Override
  public SpDataStream getDataStreamByAppId(String appId) {
    return new Cloner().mapSequence(getByAppId(inMemoryDataStreamStorage, appId));
  }

  @Override
  public SpDataStream getDataStreamById(String rdfId) {
    return new Cloner().mapSequence(inMemoryDataStreamStorage.get(rdfId));
  }

  @Override
  public DataProcessorDescription getDataProcessorById(String rdfId) {
    return new DataProcessorDescription(inMemoryDataProcessorStorage.get(rdfId));
  }

  @Override
  public DataProcessorDescription getDataProcessorById(URI rdfId) {
    return new DataProcessorDescription(inMemoryDataProcessorStorage.get(rdfId.toString()));
  }

  @Override
  public DataProcessorDescription getDataProcessorByAppId(String appId) {
    return new DataProcessorDescription(getByAppId(inMemoryDataProcessorStorage, appId));
  }

  @Override
  public DataSinkDescription getDataSinkById(String rdfId) {
    return new DataSinkDescription(inMemoryDataSinkStorage.get(rdfId));
  }

  @Override
  public DataSinkDescription getDataSinkById(URI rdfId) {
    return new DataSinkDescription(inMemoryDataSinkStorage.get(rdfId.toString()));
  }

  @Override
  public DataSinkDescription getDataSinkByAppId(String appId) {
    return new DataSinkDescription(getByAppId(inMemoryDataSinkStorage, appId));
  }

  private <T extends NamedStreamPipesEntity> T getByAppId(Map<String, T> inMemoryStorage, String appId) {
    Optional<T> entity = inMemoryStorage
            .values()
            .stream()
            .filter(d -> d.getAppId() != null)
            .filter(d -> d.getAppId().equals(appId))
            .findFirst();

    return entity.orElse(null);
  }

  @Override
  public List<SpDataStream> getAllDataStreams() {
    return new ArrayList<>(inMemoryDataStreamStorage.values());
  }

  @Override
  public List<DataProcessorDescription> getAllDataProcessors() {
    return new ArrayList<>(inMemoryDataProcessorStorage.values());
  }

  @Override
  public boolean deleteDataStream(SpDataStream sep) {
    boolean success = sesameStorage.deleteDataStream(sep);
    initializeDataStreamStorage();
    return success;
  }

  @Override
  public boolean deleteDataStream(String rdfId) {
    boolean success = sesameStorage.deleteDataStream(rdfId);
    initializeDataStreamStorage();
    return success;
  }

  @Override
  public boolean deleteDataSink(String rdfId) {
    boolean success = sesameStorage.deleteDataSink(rdfId);
    initializeDataSinkStorage();
    return success;
  }

  @Override
  public boolean deleteDataProcessor(DataProcessorDescription sepa) {
    boolean success = sesameStorage.deleteDataProcessor(sepa);
    initializeDataProcessorStorage();
    return success;
  }

  @Override
  public boolean deleteDataProcessor(String rdfId) {
    boolean success = sesameStorage.deleteDataProcessor(rdfId);
    initializeDataProcessorStorage();
    return success;
  }

  @Override
  public boolean exists(SpDataStream stream) {
    return inMemoryDataStreamStorage.containsKey(stream.getElementId());
  }

  @Override
  public boolean exists(DataProcessorDescription sepa) {
    return inMemoryDataProcessorStorage.containsKey(sepa.getElementId());
  }

  @Override
  public boolean existsDataProcessor(String elementId) {
    return inMemoryDataProcessorStorage.containsKey(elementId);
  }

  @Override
  public boolean existsDataStream(String elementId) {
    return inMemoryDataStreamStorage.containsKey(elementId);
  }

  @Override
  public boolean existsDataSink(String elementId) {
    return inMemoryDataSinkStorage.containsKey(elementId);
  }

  @Override
  public boolean update(SpDataStream sep) {
    boolean success = sesameStorage.update(sep);
    initializeDataStreamStorage();
    return success;
  }

  @Override
  public boolean update(DataProcessorDescription sepa) {
    boolean success = sesameStorage.update(sepa);
    initializeDataProcessorStorage();
    return success;
  }

  @Override
  public boolean exists(DataSinkDescription sec) {
    return inMemoryDataSinkStorage.containsKey(sec.getElementId());
  }

  @Override
  public boolean update(DataSinkDescription sec) {
    boolean success = sesameStorage.update(sec);
    initializeDataSinkStorage();
    return success;
  }

  @Override
  public boolean deleteDataSink(DataSinkDescription sec) {
    boolean success = sesameStorage.deleteDataSink(sec);
    initializeDataSinkStorage();
    return success;
  }

  @Override
  public boolean storeDataSink(DataSinkDescription sec) {
    return sesameStorage.storeDataSink(sec);
  }

  @Override
  public List<DataSinkDescription> getAllDataSinks() {
    return new ArrayList<>(inMemoryDataSinkStorage.values());
  }

  @Override
  public StaticProperty getStaticPropertyById(String rdfId) {
    return sesameStorage.getStaticPropertyById(rdfId);
  }

  @Override
  public SpDataStream getEventStreamById(String rdfId) {
    return inMemoryDataStreamStorage.get(rdfId);
  }

  @Override
  public void refreshDataProcessorCache() {
    this.initializeDataProcessorStorage();
  }

  @Override
  public void refreshDataSinkCache() {
    this.initializeDataSinkStorage();
  }

  @Override
  public void refreshDataSourceCache() {
    this.initializeDataStreamStorage();
  }
}
