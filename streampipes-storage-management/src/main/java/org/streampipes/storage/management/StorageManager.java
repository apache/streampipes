package org.streampipes.storage.management;

import org.eclipse.rdf4j.repository.Repository;
import org.streampipes.storage.Rdf4JStorageManager;
import org.streampipes.storage.api.IBackgroundKnowledgeStorage;
import org.streampipes.storage.api.IOntologyContextStorage;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;

public enum StorageManager {

  INSTANCE;

  public IPipelineElementDescriptionStorage getStorageAPI() {
    return Rdf4JStorageManager.INSTANCE.getStorageAPI();
  }

  public IBackgroundKnowledgeStorage getBackgroundKnowledgeStorage() {
    return Rdf4JStorageManager.INSTANCE.getBackgroundKnowledgeStorage();
  }

  public Repository getRepository() {
    return Rdf4JStorageManager.INSTANCE.getRepository();
  }

  public IOntologyContextStorage getContextStorage() {
    return Rdf4JStorageManager.INSTANCE.getContextStorage();
  }



}
