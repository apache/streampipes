package de.fzi.cep.sepa.storage.util;

import java.util.List;

import com.clarkparsia.empire.util.EmpireUtil;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class StorageUtils {
	
	public static void fixEmpire()
	{
		 List<SEP> list = EmpireUtil.all(StorageManager.INSTANCE.getEntityManager(), 
				 SEP.class); 
	}
	
}
