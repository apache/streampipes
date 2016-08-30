package de.fzi.cep.sepa.storage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

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
import de.fzi.cep.sepa.storage.api.AppStorage;
import de.fzi.cep.sepa.storage.api.BackgroundKnowledgeStorage;
import de.fzi.cep.sepa.storage.api.ConnectionStorage;
import de.fzi.cep.sepa.storage.api.ContextStorage;
import de.fzi.cep.sepa.storage.api.MonitoringDataStorage;
import de.fzi.cep.sepa.storage.api.NotificationStorage;
import de.fzi.cep.sepa.storage.api.PipelineCategoryStorage;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.impl.AppStorageImpl;
import de.fzi.cep.sepa.storage.impl.BackgroundKnowledgeStorageImpl;
import de.fzi.cep.sepa.storage.impl.ConnectionStorageImpl;
import de.fzi.cep.sepa.storage.impl.ContextStorageImpl;
import de.fzi.cep.sepa.storage.impl.InMemoryStorage;
import de.fzi.cep.sepa.storage.impl.MonitoringDataStorageImpl;
import de.fzi.cep.sepa.storage.impl.NotificationStorageImpl;
import de.fzi.cep.sepa.storage.impl.PipelineCategoryStorageImpl;
import de.fzi.cep.sepa.storage.impl.PipelineStorageImpl;
import de.fzi.cep.sepa.storage.impl.SesameStorageRequests;
import de.fzi.cep.sepa.storage.impl.UserStorage;
import de.fzi.cep.sepa.storage.service.UserService;
import de.fzi.cep.sepa.storage.util.StorageUtils;

public enum StorageManager {

	INSTANCE;

	private String SERVER = Configuration.getInstance().SESAME_URI;
	private String REPOSITORY_ID = Configuration.getInstance().SESAME_REPOSITORY_ID;
	
	private EntityManager storageManager;

	private RepositoryConnection conn;
	
	private Repository repository;
	private Repository bkrepo;
	
	private InMemoryStorage inMemoryStorage;
	private BackgroundKnowledgeStorage backgroundKnowledgeStorage;
	
	private boolean inMemoryInitialized = false;

	StorageManager() {
		initStorage();
		initEmpire();
		//initDbPediaEndpoint();
		initBackgroundKnowledgeStorage();
	}

	private void initBackgroundKnowledgeStorage() {
		//File dataDir = new File("C:\\temp\\myRepository\\");
		//Repository repo = new SailRepository( new MemoryStore(dataDir) );
		bkrepo = new HTTPRepository(SERVER, REPOSITORY_ID);
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
			repository = new HTTPRepository(SERVER, REPOSITORY_ID);	
			conn = repository.getConnection();
				
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean initEmpire() {
		
		try {
		EmpireOptions.STRICT_MODE = false;
		EmpireConfiguration empireCfg = new EmpireConfiguration(); 
		empireCfg.setAnnotationProvider(CustomAnnotationProvider.class); 
			
	    Empire.init(empireCfg, new OpenRdfEmpireModule()); 
	    Map<Object, Object> map = new HashMap<Object,Object>(); 
	    
	    map.put(RepositoryFactoryKeys.REPO_HANDLE, repository); 
	    map.put(ConfigKeys.FACTORY, "sesame");
	    map.put(ConfigKeys.NAME, "sepa-server");
	    map.put("url", SERVER);
	    map.put("repo", REPOSITORY_ID);
	    
	    PersistenceProvider provider = Empire.get().persistenceProvider(); 
	    storageManager = provider.createEntityManagerFactory("sepa-server", map).createEntityManager(); 
	   
		return true;
		} catch (Exception e)
		{
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
		if (!inMemoryInitialized)
		{
			this.inMemoryStorage = new InMemoryStorage(getSesameStorage());
			inMemoryInitialized = true;
		}
		return this.inMemoryStorage;
		
	}
	
	public EntityManager getEntityManager()
	{
		return storageManager;
	}
	
	public PipelineStorage getPipelineStorageAPI() {
		return new PipelineStorageImpl();
	}
	
	public StorageRequests getSesameStorage() {
		StorageUtils.fixEmpire();
		return new SesameStorageRequests();
	}
	
	public BackgroundKnowledgeStorage getBackgroundKnowledgeStorage() {
		return this.backgroundKnowledgeStorage;
	}
	
	public ConnectionStorage getConnectionStorageApi() {
		return new ConnectionStorageImpl();
	}

	public UserStorage getUserStorageAPI() { return new UserStorage(); }
	
	public UserService getUserService() {
		return new UserService(getUserStorageAPI());
	}

	public MonitoringDataStorage getMonitoringDataStorageApi()
	{
		return new MonitoringDataStorageImpl();
	}
	
	public NotificationStorage getNotificationStorageApi()
	{
		return new NotificationStorageImpl();
	}
	
	public PipelineCategoryStorage getPipelineCategoryStorageApi() {
		return new PipelineCategoryStorageImpl();
	}
	
	public AppStorage getAppStorageApi() {
		return new AppStorageImpl();
	}
	
	public ContextStorage getContextStorage() {
		return new ContextStorageImpl(bkrepo);
	}


}
