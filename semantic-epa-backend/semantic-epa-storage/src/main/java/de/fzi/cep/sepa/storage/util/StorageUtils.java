package de.fzi.cep.sepa.storage.util;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import com.clarkparsia.empire.util.EmpireUtil;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class StorageUtils {

	public static boolean emptyRepository(RepositoryConnection conn) {
		try {

			RepositoryResult<Statement> rresult = conn.getStatements(null, null, null, true);
			while (rresult.hasNext()) {
				Statement t = rresult.next();
				StorageManager.INSTANCE.getTempConnection().remove(t);
			}
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}
	
	public static void fixEmpire()
	{
		 List<SEP> list = EmpireUtil.all(StorageManager.INSTANCE.getEntityManager(), 
				 SEP.class); 
	}
	
	public static void fixTempEmpire()
	{
		 List<SEP> list = EmpireUtil.all(StorageManager.INSTANCE.getTempEntityManager(), 
				 SEP.class); 
	}
}
