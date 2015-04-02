package de.fzi.cep.sepa.storage.controller;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.sesame.OpenRdfEmpireModule;

import de.fzi.cep.sepa.commons.Configuration;
import de.fzi.cep.sepa.storage.PipelineStorageImpl;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.impl.StorageRequestsImpl;
import de.fzi.cep.sepa.storage.util.StorageUtils;

public enum StorageManager {

	INSTANCE;

	private String SERVER = "http://localhost:8080/openrdf-sesame";
	private String REPOSITORY_ID = "test-6";
	private String TEMP_REPOSITORY_ID = "temp-db";
	
	private EntityManager storageManager;
	private EntityManager tempStorageManager;

	private RepositoryConnection conn;
	private RepositoryConnection tempConn;
	
	private Repository repository;
	private Repository tempRepository;

	StorageManager() {
		initStorage();
		initEmpire();
	}

	private boolean initStorage() {

		try {
			repository = new HTTPRepository(SERVER, REPOSITORY_ID);
			tempRepository = new HTTPRepository(SERVER, TEMP_REPOSITORY_ID);
				
			
			conn = repository.getConnection();
			tempConn = tempRepository.getConnection();
				
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean initEmpire() {
		
		try {
		System.setProperty(
				"empire.configuration.file",
				Configuration.EMPIRE_CONFIG_LOCATION);
        /*System.setProperty("empire.configuration.file",
                "/home/robin/FZI/CEP/semantic-epa-parent/semantic-epa-backend/semantic-epa-storage/src/main/resources/empire.config.properties");*/
			
		Empire.init(new OpenRdfEmpireModule());

		// create an EntityManager for the specified persistence context
		storageManager = Persistence.createEntityManagerFactory(
				"sepa-server").createEntityManager();
		
		tempStorageManager = Persistence.createEntityManagerFactory("temp-db").createEntityManager();
		
		return true;
		} catch (Exception e)
		{
			return false;
		}
		
	}

	public RepositoryConnection getConnection() {
		return conn;
	}
	
	public RepositoryConnection getTempConnection() {
		return tempConn;
	}

	public StorageRequests getStorageAPI() {
		StorageUtils.fixEmpire();
		return new StorageRequestsImpl();
	}
	
	public EntityManager getEntityManager()
	{
		return storageManager;
	}
	
	public EntityManager getTempEntityManager()
	{
		return tempStorageManager;
	}
	
	public PipelineStorage getPipelineStorageAPI() {
		//return new PipelineStorageImpl();
		//add storage implementation
		return new PipelineStorageImpl();
	}
	
}
