package org.streampipes.manager.storage;

import org.eclipse.rdf4j.repository.Repository;
import org.streampipes.storage.Rdf4JStorageManager;
import org.streampipes.storage.api.IBackgroundKnowledgeStorage;
import org.streampipes.storage.api.IPipelineElementConnectionStorage;
import org.streampipes.storage.api.IOntologyContextStorage;
import org.streampipes.storage.api.IPipelineMonitoringDataStorage;
import org.streampipes.storage.api.INotificationStorage;
import org.streampipes.storage.api.IPipelineCategoryStorage;
import org.streampipes.storage.api.IPipelineStorage;
import org.streampipes.storage.api.IRdfEndpointStorage;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.api.IVisualizationStorage;
import org.streampipes.storage.couchdb.impl.ConnectionStorageImpl;
import org.streampipes.storage.couchdb.impl.MonitoringDataStorageImpl;
import org.streampipes.storage.couchdb.impl.NotificationStorageImpl;
import org.streampipes.storage.couchdb.impl.PipelineCategoryStorageImpl;
import org.streampipes.storage.couchdb.impl.PipelineStorageImpl;
import org.streampipes.storage.couchdb.impl.RdfEndpointStorageImpl;
import org.streampipes.storage.couchdb.impl.UserStorage;
import org.streampipes.storage.couchdb.impl.VisualizationStorageImpl;

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


    public IPipelineStorage getPipelineStorageAPI() {
        return new PipelineStorageImpl();
    }


    public IPipelineElementConnectionStorage getConnectionStorageApi() {
        return new ConnectionStorageImpl();
    }

    public UserStorage getUserStorageAPI() {
        return new UserStorage();
    }

    public UserService getUserService() {
        return new UserService(getUserStorageAPI());
    }

    public IPipelineMonitoringDataStorage getMonitoringDataStorageApi() {
        return new MonitoringDataStorageImpl();
    }

    public INotificationStorage getNotificationStorageApi() {
        return new NotificationStorageImpl();
    }

    public IPipelineCategoryStorage getPipelineCategoryStorageApi() {
        return new PipelineCategoryStorageImpl();
    }

    public IVisualizationStorage getVisualizationStorageApi() {
        return new VisualizationStorageImpl();
    }


    public IRdfEndpointStorage getRdfEndpointStorage() {
        return new RdfEndpointStorageImpl();
    }

}
