package org.streampipes.manager.storage;

import org.eclipse.rdf4j.repository.Repository;
import org.streampipes.storage.Rdf4JStorageManager;
import org.streampipes.storage.api.BackgroundKnowledgeStorage;
import org.streampipes.storage.api.ConnectionStorage;
import org.streampipes.storage.api.ContextStorage;
import org.streampipes.storage.api.MonitoringDataStorage;
import org.streampipes.storage.api.NotificationStorage;
import org.streampipes.storage.api.PipelineCategoryStorage;
import org.streampipes.storage.api.PipelineStorage;
import org.streampipes.storage.api.RdfEndpointStorage;
import org.streampipes.storage.api.StorageRequests;
import org.streampipes.storage.api.VisualizationStorage;
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

    public StorageRequests getStorageAPI() {
      return Rdf4JStorageManager.INSTANCE.getStorageAPI();
    }

    public BackgroundKnowledgeStorage getBackgroundKnowledgeStorage() {
      return Rdf4JStorageManager.INSTANCE.getBackgroundKnowledgeStorage();
  }

  public Repository getRepository() {
      return Rdf4JStorageManager.INSTANCE.getRepository();
  }

  public ContextStorage getContextStorage() {
      return Rdf4JStorageManager.INSTANCE.getContextStorage();
  }


    public PipelineStorage getPipelineStorageAPI() {
        return new PipelineStorageImpl();
    }


    public ConnectionStorage getConnectionStorageApi() {
        return new ConnectionStorageImpl();
    }

    public UserStorage getUserStorageAPI() {
        return new UserStorage();
    }

    public UserService getUserService() {
        return new UserService(getUserStorageAPI());
    }

    public MonitoringDataStorage getMonitoringDataStorageApi() {
        return new MonitoringDataStorageImpl();
    }

    public NotificationStorage getNotificationStorageApi() {
        return new NotificationStorageImpl();
    }

    public PipelineCategoryStorage getPipelineCategoryStorageApi() {
        return new PipelineCategoryStorageImpl();
    }

    public VisualizationStorage getVisualizationStorageApi() {
        return new VisualizationStorageImpl();
    }


    public RdfEndpointStorage getRdfEndpointStorage() {
        return new RdfEndpointStorageImpl();
    }

}
