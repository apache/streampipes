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
package org.streampipes.storage;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.empire.core.empire.Empire;
import org.streampipes.empire.core.empire.EmpireOptions;
import org.streampipes.empire.core.empire.config.ConfigKeys;
import org.streampipes.empire.core.empire.config.EmpireConfiguration;
import org.streampipes.empire.rdf4j.OpenRdfEmpireModule;
import org.streampipes.empire.rdf4j.RepositoryFactoryKeys;
import org.streampipes.serializers.jsonld.CustomAnnotationProvider;
import org.streampipes.storage.api.IBackgroundKnowledgeStorage;
import org.streampipes.storage.api.IOntologyContextStorage;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.api.ITripleStorage;
import org.streampipes.storage.rdf4j.config.Rdf4JConfig;
import org.streampipes.storage.rdf4j.impl.BackgroundKnowledgeStorageImpl;
import org.streampipes.storage.rdf4j.impl.ContextStorageImpl;
import org.streampipes.storage.rdf4j.impl.PipelineElementInMemoryStorage;
import org.streampipes.storage.rdf4j.impl.PipelineElementStorageRequests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

public enum Rdf4JStorageManager implements ITripleStorage {

  INSTANCE;

  private final Logger LOG = LoggerFactory.getLogger(Rdf4JStorageManager.class);

  private Repository pipelineElementRepository;
  private Repository backgroundKnowledgeRepository;

  private PipelineElementInMemoryStorage pipelineElementInMemoryStorage;
  private IBackgroundKnowledgeStorage backgroundKnowledgeStorage;

  Rdf4JStorageManager() {
    initPipelineElementStorage();
    initBackgroundKnowledgeStorage();
  }

  private void initBackgroundKnowledgeStorage() {
    backgroundKnowledgeRepository = makeRepo(Rdf4JConfig
            .INSTANCE
            .getBackgroundKnowledgeStorageLocation());
    try {
      backgroundKnowledgeRepository.initialize();
      this.backgroundKnowledgeStorage =
              new BackgroundKnowledgeStorageImpl(backgroundKnowledgeRepository);
    } catch (RepositoryException e) {
      LOG.error("Could not initialize background knowledge repository", e);
    }
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
    MemoryStore memoryStore = new MemoryStore();
    memoryStore.setPersist(true);
    memoryStore.setSyncDelay(1000);
    memoryStore.setDataDir(new File(storageDir));
    return new SailRepository(new ForwardChainingRDFSInferencer(memoryStore));
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
  public IPipelineElementDescriptionStorage getPipelineElementStorage() {
    return this.pipelineElementInMemoryStorage;
  }

  @Override
  public IOntologyContextStorage getContextStorage() {
    return new ContextStorageImpl(backgroundKnowledgeRepository);
  }

}
