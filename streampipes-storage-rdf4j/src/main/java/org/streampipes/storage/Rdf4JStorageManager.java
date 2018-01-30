/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.storage;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
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
import org.streampipes.storage.rdf4j.impl.BackgroundKnowledgeStorageImpl;
import org.streampipes.storage.rdf4j.impl.ContextStorageImpl;
import org.streampipes.storage.rdf4j.impl.InMemoryStorage;
import org.streampipes.storage.rdf4j.impl.SesameStorageRequests;
import org.streampipes.storage.rdf4j.util.SesameConfig;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

public enum Rdf4JStorageManager {

  INSTANCE;

  private EntityManager storageManager;

  private Repository repository;
  private Repository bkrepo;

  private InMemoryStorage inMemoryStorage;
  private IBackgroundKnowledgeStorage backgroundKnowledgeStorage;

  private boolean inMemoryInitialized = false;

  Rdf4JStorageManager() {
    initSesameDatabases();
  }

  public void initSesameDatabases() {
    initStorage();
    initEmpire();
    initBackgroundKnowledgeStorage();
  }

  private void initBackgroundKnowledgeStorage() {
    bkrepo = new HTTPRepository(SesameConfig.INSTANCE.getUri(),
            SesameConfig.INSTANCE.getRepositoryId());
    try {
      bkrepo.initialize();
      this.backgroundKnowledgeStorage = new BackgroundKnowledgeStorageImpl(bkrepo);
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private boolean initStorage() {
    try {
      repository = new HTTPRepository(SesameConfig.INSTANCE.getUri(),
              SesameConfig.INSTANCE.getRepositoryId());

      initEmpire();

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }

  private boolean initEmpire() {

    try {
      EmpireOptions.STRICT_MODE = false;
      EmpireConfiguration empireCfg = new EmpireConfiguration();
      empireCfg.setAnnotationProvider(CustomAnnotationProvider.class);

      Empire.init(empireCfg, new OpenRdfEmpireModule());
      Map<Object, Object> map = new HashMap<Object, Object>();

      map.put(RepositoryFactoryKeys.REPO_HANDLE, repository);
      map.put(ConfigKeys.FACTORY, "sesame");
      map.put(ConfigKeys.NAME, "sepa-server");
      map.put("url", SesameConfig.INSTANCE.getUri());
      map.put("repo", SesameConfig.INSTANCE.getRepositoryId());

      PersistenceProvider provider = Empire.get().persistenceProvider();
      storageManager = provider.createEntityManagerFactory("sepa-server", map).createEntityManager();

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }

  public IBackgroundKnowledgeStorage getBackgroundKnowledgeStorage() {
    if (backgroundKnowledgeStorage == null) {
      initSesameDatabases();
    }
    return this.backgroundKnowledgeStorage;
  }

  public Repository getRepository() {
    return bkrepo;
  }

  public IPipelineElementDescriptionStorage getStorageAPI() {
    if (backgroundKnowledgeStorage == null) {
      initSesameDatabases();
    }
    if (!inMemoryInitialized) {
      this.inMemoryStorage = new InMemoryStorage(getSesameStorage());
      inMemoryInitialized = true;
    }
    return this.inMemoryStorage;

  }

  public EntityManager getEntityManager() {
    return storageManager;
  }

  public IOntologyContextStorage getContextStorage() {
    return new ContextStorageImpl(bkrepo);
  }

  public IPipelineElementDescriptionStorage getSesameStorage() {
    return new SesameStorageRequests();
  }

}
