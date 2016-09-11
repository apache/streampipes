package de.fzi.cep.sepa.storage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import de.fzi.cep.sepa.storage.api.*;
import de.fzi.cep.sepa.storage.impl.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.sesame.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesame.RepositoryFactoryKeys;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.model.transform.CustomAnnotationProvider;
import de.fzi.cep.sepa.storage.service.UserService;

public enum StorageManager {

    INSTANCE;

    private EntityManager storageManager;

    private RepositoryConnection conn;

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
        bkrepo = new HTTPRepository(Configuration.getInstance().SERVER_URL,
                Configuration.getInstance().SESAME_REPOSITORY_ID);
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
            repository = new HTTPRepository(Configuration.getInstance().SERVER_URL,
                    Configuration.getInstance().SESAME_REPOSITORY_ID);
            conn = repository.getConnection();

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
            map.put("url", Configuration.getInstance().SERVER_URL);
            map.put("repo", Configuration.getInstance().SESAME_REPOSITORY_ID);

            PersistenceProvider provider = Empire.get().persistenceProvider();
            storageManager = provider.createEntityManagerFactory("sepa-server", map).createEntityManager();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public RepositoryConnection getConnection() {
        return conn;
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
        //StorageUtils.fixEmpire();
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

    public AppStorage getAppStorageApi() {
        return new AppStorageImpl();
    }

    public VisualizationStorage getVisualizationStorageApi() {
        return new VisualizationStorageImpl();
    }

    public ContextStorage getContextStorage() {
        return new ContextStorageImpl(bkrepo);
    }

}
