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

package org.streampipes.storage.rdf4j.impl;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.api.IPipelineElementDescriptionStorageCache;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PipelineElementInMemoryStorage implements IPipelineElementDescriptionStorageCache {

  private Map<String, DataSinkDescription> inMemoryDataSinkStorage;
  private Map<String, DataSourceDescription> inMemoryDataSourceStorage;
  private Map<String, DataProcessorDescription> inMemoryDataProcessorStorage;
  private Map<String, SpDataStream> inMemoryEventStreamStorage;
  private IPipelineElementDescriptionStorage sesameStorage;


  public PipelineElementInMemoryStorage(IPipelineElementDescriptionStorage sesameStorage) {
    this.inMemoryDataSinkStorage = new HashMap<>();
    this.inMemoryDataProcessorStorage = new HashMap<>();
    this.inMemoryDataSourceStorage = new HashMap<>();
    this.inMemoryEventStreamStorage = new HashMap<>();
    this.sesameStorage = sesameStorage;
    init();
  }

  private void init() {
    initializeSECStorage();
    initializeSEPAStorage();
    initializeSEPStorage();
  }

  private void initializeSECStorage() {
    inMemoryDataSinkStorage.clear();
    List<DataSinkDescription> secs = sort(sesameStorage
            .getAllDataSinks()
            .stream()
            .map(DataSinkDescription::new)
            .collect(Collectors.toList()));
    secs.forEach(sec -> inMemoryDataSinkStorage.put(sec.getElementId(), sec));
  }

  private void initializeSEPAStorage() {
    inMemoryDataProcessorStorage.clear();
    List<DataProcessorDescription> sepas = sort(sesameStorage
            .getAllDataProcessors()
            .stream()
            .map(DataProcessorDescription::new)
            .collect(Collectors.toList()));
    sepas.forEach(sepa -> inMemoryDataProcessorStorage.put(sepa.getElementId(), sepa));
  }

  private void initializeSEPStorage() {
    inMemoryDataSourceStorage.clear();
    List<DataSourceDescription> seps = sesameStorage.getAllDataSources();
    seps.forEach(sep ->
            sep.getSpDataStreams().forEach(es ->
                    es.getEventSchema()
                            .getEventProperties()
                            .sort(Comparator.comparingInt(EventProperty::getIndex))));
    seps.forEach(sep -> inMemoryDataSourceStorage.put(sep.getElementId(), sep));
    seps.forEach(sep -> sep.getSpDataStreams().forEach(eventStream -> inMemoryEventStreamStorage.put(eventStream.getElementId(),
            eventStream)));
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
  public boolean storeDataSource(DataSourceDescription sep) {
    return sesameStorage.storeDataSource(sep);
  }

  @Override
  public boolean storeDataSource(String jsonld) {
    return sesameStorage.storeDataSource(jsonld);
  }

  @Override
  public boolean storeDataProcessor(DataProcessorDescription sepa) {
    return sesameStorage.storeDataProcessor(sepa);
  }

  @Override
  public boolean storeDataProcessor(String jsonld) {
    return sesameStorage.storeDataSource(jsonld);
  }

  @Override
  public DataSourceDescription getDataSourceById(URI rdfId) {
    return new DataSourceDescription(inMemoryDataSourceStorage.get(rdfId.toString()));
  }

  @Override
  public DataSourceDescription getDataSourceByAppId(String appId) {
    return new DataSourceDescription(getByAppId(inMemoryDataSourceStorage, appId));
  }

  @Override
  public DataSourceDescription getDataSourceById(String rdfId) {
    return new DataSourceDescription(inMemoryDataSourceStorage.get(rdfId));
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
  public List<DataSourceDescription> getAllDataSources() {
    return new ArrayList<>(inMemoryDataSourceStorage.values());
  }

  @Override
  public List<DataProcessorDescription> getAllDataProcessors() {
    return new ArrayList<>(inMemoryDataProcessorStorage.values());
  }

  @Override
  public boolean deleteDataSource(DataSourceDescription sep) {
    boolean success = sesameStorage.deleteDataSource(sep);
    initializeSEPStorage();
    return success;
  }

  @Override
  public boolean deleteDataSource(String rdfId) {
    boolean success = sesameStorage.deleteDataSource(rdfId);
    initializeSEPStorage();
    return success;
  }

  @Override
  public boolean deleteDataSink(String rdfId) {
    boolean success = sesameStorage.deleteDataSink(rdfId);
    initializeSECStorage();
    return success;
  }

  @Override
  public boolean deleteDataProcessor(DataProcessorDescription sepa) {
    boolean success = sesameStorage.deleteDataProcessor(sepa);
    initializeSEPAStorage();
    return success;
  }

  @Override
  public boolean deleteDataProcessor(String rdfId) {
    boolean success = sesameStorage.deleteDataSource(rdfId);
    initializeSEPAStorage();
    return success;
  }

  @Override
  public boolean exists(DataSourceDescription sep) {
    return inMemoryDataSourceStorage.containsKey(sep.getElementId());
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
  public boolean existsDataSource(String elementId) {
    return inMemoryDataSourceStorage.containsKey(elementId);
  }

  @Override
  public boolean existsDataSink(String elementId) {
    return inMemoryDataSinkStorage.containsKey(elementId);
  }

  @Override
  public boolean update(DataSourceDescription sep) {
    boolean success = sesameStorage.update(sep);
    initializeSEPStorage();
    return success;
  }

  @Override
  public boolean update(DataProcessorDescription sepa) {
    boolean success = sesameStorage.update(sepa);
    initializeSEPAStorage();
    return success;
  }

  @Override
  public boolean exists(DataSinkDescription sec) {
    return inMemoryDataSinkStorage.containsKey(sec.getElementId());
  }

  @Override
  public boolean update(DataSinkDescription sec) {
    boolean success = sesameStorage.update(sec);
    initializeSECStorage();
    return success;
  }

  @Override
  public boolean deleteDataSink(DataSinkDescription sec) {
    boolean success = sesameStorage.deleteDataSink(sec);
    initializeSECStorage();
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
    return inMemoryEventStreamStorage.get(rdfId);
  }

  @Override
  public void refreshDataProcessorCache() {
    this.initializeSEPAStorage();
  }

  @Override
  public void refreshDataSinkCache() {
    this.initializeSECStorage();
  }

  @Override
  public void refreshDataSourceCache() {
    this.initializeSEPStorage();
  }
}
