package org.streampipes.model.transform;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.sesame.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesame.RepositoryFactoryKeys;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

public enum EmpireManager {

	INSTANCE;
	
	private EntityManager tempStorageManager;
	private RepositoryConnection tempConn;
	
	EmpireManager()
	{
		initTempStore();
	}
	
	public void initTempStore()
	{
		Repository repository = new SailRepository(new MemoryStore());	
		try {
			repository.initialize();
			tempConn = repository.getConnection();

			EmpireConfiguration empireCfg = new EmpireConfiguration(); 
			empireCfg.setAnnotationProvider(CustomAnnotationProvider.class); 
			
			//Map<String,String> props = empireCfg.getGlobalConfig();
	        //props.put("com.clarkparsia.empire.annotation.RdfsClass", CustomAnnotationProvider.getAnnotatedClassesAsString());
			EmpireOptions.STRICT_MODE = false;
		    Empire.init(empireCfg, new OpenRdfEmpireModule()); 
		    Map<Object, Object> map = new HashMap<Object,Object>(); 
		    map.put(RepositoryFactoryKeys.REPO_HANDLE, repository); 
		    map.put(ConfigKeys.FACTORY, "sesame");
		    map.put(ConfigKeys.NAME, "temp-db2");
		    PersistenceProvider provider = Empire.get().persistenceProvider(); 
		    tempStorageManager = provider.createEntityManagerFactory("temp-db2", map).createEntityManager(); 
		} catch (RepositoryException e) {
			
			e.printStackTrace();
		}
	}
	
	public RepositoryConnection getTempConnection() {
		return tempConn;
	}
	
	public EntityManager getTempEntityManager()
	{
		return tempStorageManager;
	}
		
	public boolean clearRepository() {
		try {

			RepositoryResult<Statement> rresult = tempConn.getStatements(null, null, null, true);
			while (rresult.hasNext()) {
				Statement t = rresult.next();
				tempConn.remove(t);
			}
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}
}
