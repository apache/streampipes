package org.streampipes.storage.util;

import com.clarkparsia.empire.util.EmpireUtil;

import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.storage.controller.StorageManager;

public class StorageUtils {
	
	public static void fixEmpire()
	{
		 EmpireUtil.all(StorageManager.INSTANCE.getEntityManager(), 
				 SepDescription.class); 
	}
}
