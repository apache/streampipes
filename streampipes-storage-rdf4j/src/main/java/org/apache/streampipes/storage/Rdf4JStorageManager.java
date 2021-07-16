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
package org.apache.streampipes.storage;

import io.fogsy.empire.core.empire.Empire;
import io.fogsy.empire.core.empire.EmpireOptions;
import io.fogsy.empire.core.empire.config.ConfigKeys;
import io.fogsy.empire.core.empire.config.EmpireConfiguration;
import io.fogsy.empire.rdf4j.OpenRdfEmpireModule;
import io.fogsy.empire.rdf4j.RepositoryFactoryKeys;
import org.apache.streampipes.serializers.jsonld.CustomAnnotationProvider;
import org.apache.streampipes.storage.api.IBackgroundKnowledgeStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.api.ITripleStorage;
import org.apache.streampipes.storage.rdf4j.config.Rdf4JConfig;
import org.apache.streampipes.storage.rdf4j.impl.PipelineElementInMemoryStorage;
import org.apache.streampipes.storage.rdf4j.impl.PipelineElementStorageRequests;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Rdf4JStorageManager implements ITripleStorage {

  INSTANCE;

  private final Logger LOG = LoggerFactory.getLogger(Rdf4JStorageManager.class);

  private Repository pipelineElementRepository;
  private Repository backgroundKnowledgeRepository;

  private IPipelineElementDescriptionStorageCache pipelineElementInMemoryStorage;
  private IBackgroundKnowledgeStorage backgroundKnowledgeStorage;

  Rdf4JStorageManager() {
    initPipelineElementStorage();
  }

  private void initPipelineElementStorage() {
    try {
      pipelineElementRepository = makeRepo(Rdf4JConfig.INSTANCE.getPipelineElementStorageLocation());
      pipelineElementRepository.initialize();

      initEmpire();
    } catch (Exception e) {
      LOG.error("Could not initialize pipeline element repository", e);
    }
  }

  private void initEmpire() {
    try {
      EmpireOptions.STRICT_MODE = false;
      EmpireConfiguration empireCfg = new EmpireConfiguration();
      empireCfg.setAnnotationProvider(CustomAnnotationProvider.class);

      Empire.init(empireCfg, new OpenRdfEmpireModule());
      Map<Object, Object> configMap = new HashMap<>();

      configMap.put(RepositoryFactoryKeys.REPO_HANDLE, pipelineElementRepository);
      configMap.put(ConfigKeys.FACTORY, "sesame");
      configMap.put(ConfigKeys.NAME, "streampipes-server");

      PersistenceProvider provider = Empire.get().persistenceProvider();
      EntityManager storageManager =
              provider.createEntityManagerFactory("streampipes-server", configMap).createEntityManager();
      PipelineElementStorageRequests storageRequests = new PipelineElementStorageRequests(storageManager);
      this.pipelineElementInMemoryStorage = new PipelineElementInMemoryStorage(storageRequests);

    } catch (Exception e) {
      LOG.error("Could not initialize empire", e);
    }
  }

  private Repository makeRepo(String storageDir) {
    NativeStore nativeStore = new NativeStore();
    nativeStore.setDataDir(new File(storageDir));
    return new SailRepository(nativeStore);
  }

  @Override
  public IBackgroundKnowledgeStorage getBackgroundKnowledgeStorage() {
    return this.backgroundKnowledgeStorage;
  }

  @Override
  public Repository getRepository() {
    return backgroundKnowledgeRepository;
  }

  @Override
  public IPipelineElementDescriptionStorageCache getPipelineElementStorage() {
    return this.pipelineElementInMemoryStorage;
  }

}
