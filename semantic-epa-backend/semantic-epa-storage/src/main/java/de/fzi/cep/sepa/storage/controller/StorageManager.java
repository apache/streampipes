package de.fzi.cep.sepa.storage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sparql.SPARQLRepository;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.sesame.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesame.RepositoryFactoryKeys;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.model.transform.CustomAnnotationProvider;
import de.fzi.cep.sepa.storage.PipelineStorageImpl;
import de.fzi.cep.sepa.storage.api.BackgroundKnowledgeStorage;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.impl.BackgroundKnowledgeStorageImpl;
import de.fzi.cep.sepa.storage.impl.InMemoryStorage;
import de.fzi.cep.sepa.storage.impl.SesameStorageRequests;
import de.fzi.cep.sepa.storage.util.StorageUtils;

public enum StorageManager {

	INSTANCE;

	private String SERVER = Configuration.SESAME_URI;
	private String REPOSITORY_ID = Configuration.SESAME_REPOSITORY_ID;
	
	private EntityManager storageManager;

	private RepositoryConnection conn;
	private RepositoryConnection dbpediaConn;
	
	private Repository repository;
	
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
		Repository repo = new HTTPRepository(SERVER, REPOSITORY_ID);
		try {
			repo.initialize();
			this.backgroundKnowledgeStorage = new BackgroundKnowledgeStorageImpl(repo);
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
	
	private boolean initDbPediaEndpoint() {
		SPARQLRepository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		try {
			repo.initialize();
			dbpediaConn = repo.getConnection();
			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}		  
	}

	private boolean initEmpire() {
		
		try {
		
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
	
	public RepositoryConnection getDbpediaConnection() {
		return dbpediaConn;
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
	
}
