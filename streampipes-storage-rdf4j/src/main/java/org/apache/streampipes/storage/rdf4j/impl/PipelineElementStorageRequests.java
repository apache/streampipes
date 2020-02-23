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

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import io.fogsy.empire.core.empire.impl.RdfQuery;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.rdf4j.sparql.QueryBuilder;
import org.apache.streampipes.storage.rdf4j.util.Transformer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PipelineElementStorageRequests implements IPipelineElementDescriptionStorage {

  private EntityManager entityManager;

  public PipelineElementStorageRequests(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  //TODO: exception handling

  @Override
  public boolean storeDataSource(DataSourceDescription sep) {
    if (exists(sep)) {
      return false;
    }
    entityManager.persist(sep);
    return true;
  }

  @Override
  public boolean storeDataSource(String jsonld) {
    DataSourceDescription sep;
    try {
      sep = Transformer.fromJsonLd(DataSourceDescription.class, jsonld);
      return storeDataSource(sep);
    } catch (RDFParseException | IOException | RepositoryException | UnsupportedRDFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean storeDataProcessor(DataProcessorDescription sepa) {
    if (existsDataProcessor(sepa.getElementId())) {
      return false;
    }
    entityManager.persist(sepa);
    return true;
  }

  @Override
  public boolean existsDataProcessor(String rdfId) {
    return entityManager.find(DataProcessorDescription.class, rdfId) != null;
  }

  @Override
  public boolean existsDataSource(String rdfId) {
    return entityManager.find(DataSourceDescription.class, rdfId) != null;
  }

  @Override
  public boolean existsDataSink(String rdfId) {
    return entityManager.find(DataSinkDescription.class, rdfId) != null;
  }

  @Override
  public boolean storeDataProcessor(String jsonld) {
    DataProcessorDescription sepa;
    try {
      sepa = Transformer.fromJsonLd(DataProcessorDescription.class, jsonld);
      return storeDataProcessor(sepa);
    } catch (RDFParseException | IOException | RepositoryException | UnsupportedRDFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public DataSourceDescription getDataSourceById(URI rdfId) {
    return entityManager.find(DataSourceDescription.class, rdfId);
  }

  @Override
  public DataSourceDescription getDataSourceByAppId(String appId) {
    return getByAppId(getAllDataSources(), appId);
  }

  @Override
  public DataSourceDescription getDataSourceById(String rdfId) throws URISyntaxException {
    return getDataSourceById(new URI(rdfId));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DataSourceDescription> getAllDataSources() {
    Query query = entityManager.createQuery(QueryBuilder.buildListSEPQuery());
    query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataSourceDescription.class);
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DataProcessorDescription> getAllDataProcessors() {
    Query query = entityManager.createQuery(QueryBuilder.buildListSEPAQuery());
    query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataProcessorDescription.class);
    return query.getResultList();
  }

  @Override
  public boolean deleteDataSource(DataSourceDescription sep) {
    deleteDataSource(sep.getElementId());
    return true;
  }

  @Override
  public boolean deleteDataSource(String rdfId) {
    DataSourceDescription sep = entityManager.find(DataSourceDescription.class, rdfId);
    entityManager.remove(sep);
    return true;
  }

  @Override
  public boolean deleteDataProcessor(DataProcessorDescription sepa) {
    deleteDataProcessor(sepa.getElementId());
    return true;
  }

  @Override
  public boolean deleteDataProcessor(String rdfId) {
    DataProcessorDescription sepa = entityManager.find(DataProcessorDescription.class, rdfId);
    entityManager.remove(sepa);
    return true;
  }

  @Override
  public boolean exists(DataSourceDescription sep) {
    DataSourceDescription storedSEP = entityManager.find(DataSourceDescription.class, sep.getElementId());
    return storedSEP != null;
  }

  @Override
  public boolean exists(DataProcessorDescription sepa) {
    DataProcessorDescription storedSEPA = entityManager.find(DataProcessorDescription.class, sepa.getElementId());
    return storedSEPA != null;
  }

  @Override
  public boolean update(DataSourceDescription sep) {
    return deleteDataSource(sep) && storeDataSource(sep);
  }

  @Override
  public boolean update(DataProcessorDescription sepa) {
    return deleteDataProcessor(sepa) && storeDataProcessor(sepa);
  }

  @Override
  public DataProcessorDescription getDataProcessorById(String rdfId) throws URISyntaxException {
    return getDataProcessorById(new URI(rdfId));
  }

  @Override
  public DataProcessorDescription getDataProcessorById(URI rdfId) {
    return entityManager.find(DataProcessorDescription.class, rdfId);
  }

  @Override
  public DataProcessorDescription getDataProcessorByAppId(String appId) {
    return getByAppId(getAllDataProcessors(), appId);
  }

  @Override
  public DataSinkDescription getDataSinkById(String rdfId) throws URISyntaxException {
    return getDataSinkById(new URI(rdfId));
  }

  @Override
  public DataSinkDescription getDataSinkById(URI rdfId) {
    return entityManager.find(DataSinkDescription.class, rdfId);
  }

  @Override
  public DataSinkDescription getDataSinkByAppId(String appId) {
    return getByAppId(getAllDataSinks(), appId);
  }

  @Override
  public boolean exists(DataSinkDescription sec) {
    DataSinkDescription storedSEC = entityManager.find(DataSinkDescription.class, sec.getElementId());
    return storedSEC != null;
  }

  @Override
  public boolean update(DataSinkDescription sec) {
    return deleteDataSink(sec) && storeDataSink(sec);

  }

  @Override
  public boolean deleteDataSink(DataSinkDescription sec) {
    return deleteDataSink(sec.getElementId());
  }

  @Override
  public boolean deleteDataSink(String rdfId) {
    DataSinkDescription sec = entityManager.find(DataSinkDescription.class, rdfId);
    entityManager.remove(sec);
    return true;
  }

  @Override
  public boolean storeDataSink(DataSinkDescription sec) {
    if (exists(sec)) {
      return false;
    }
    entityManager.persist(sec);
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DataSinkDescription> getAllDataSinks() {
    Query query = entityManager.createQuery(QueryBuilder.buildListSECQuery());
    query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataSinkDescription.class);
    return query.getResultList();
  }

  @Override
  public StaticProperty getStaticPropertyById(String rdfId) {
    return entityManager.find(StaticProperty.class, URI.create(rdfId));
  }

  @Override
  public boolean storeInvocablePipelineElement(InvocableStreamPipesEntity element) {
    entityManager.persist(element);
    return true;
  }

  @Override
  public SpDataStream getEventStreamById(String rdfId) {
    return entityManager.find(SpDataStream.class, URI.create(rdfId));
  }

  private <T extends NamedStreamPipesEntity> T getByAppId(List<T> elements, String appId) {
    return elements
            .stream()
            .filter(e -> e.getAppId().equals(appId))
            .findFirst()
            .orElse(null);
  }


}
