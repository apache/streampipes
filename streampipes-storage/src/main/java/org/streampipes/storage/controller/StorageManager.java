package org.streampipes.storage.controller;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.sesame.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesame.RepositoryFactoryKeys;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.streampipes.model.transform.CustomAnnotationProvider;
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
import org.streampipes.storage.impl.BackgroundKnowledgeStorageImpl;
import org.streampipes.storage.impl.ConnectionStorageImpl;
import org.streampipes.storage.impl.ContextStorageImpl;
import org.streampipes.storage.impl.InMemoryStorage;
import org.streampipes.storage.impl.MonitoringDataStorageImpl;
import org.streampipes.storage.impl.NotificationStorageImpl;
import org.streampipes.storage.impl.PipelineCategoryStorageImpl;
import org.streampipes.storage.impl.PipelineStorageImpl;
import org.streampipes.storage.impl.RdfEndpointStorageImpl;
import org.streampipes.storage.impl.SesameStorageRequests;
import org.streampipes.storage.impl.UserStorage;
import org.streampipes.storage.impl.VisualizationStorageImpl;
import org.streampipes.storage.service.UserService;
import org.streampipes.storage.util.SesameConfig;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

public enum StorageManager {

    INSTANCE;

    private EntityManager storageManager;

    private Repository repository;
    private Repository bkrepo;

    private InMemoryStorage inMemoryStorage;
    private BackgroundKnowledgeStorage backgroundKnowledgeStorage;

    private boolean inMemoryInitialized = false;

    StorageManager() {
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

    public Repository getRepository() {
        return bkrepo;
    }

    public StorageRequests getStorageAPI() {
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

    public PipelineStorage getPipelineStorageAPI() {
        return new PipelineStorageImpl();
    }

    public StorageRequests getSesameStorage() {
        return new SesameStorageRequests();
    }

    public BackgroundKnowledgeStorage getBackgroundKnowledgeStorage() {
        if (backgroundKnowledgeStorage == null) {
            initSesameDatabases();
        }
        return this.backgroundKnowledgeStorage;
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

    public ContextStorage getContextStorage() {
        return new ContextStorageImpl(bkrepo);
    }

    public RdfEndpointStorage getRdfEndpointStorage() {
        return new RdfEndpointStorageImpl();
    }

}
