package org.streampipes.model.transform;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.sesame.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesame.RepositoryFactoryKeys;

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
