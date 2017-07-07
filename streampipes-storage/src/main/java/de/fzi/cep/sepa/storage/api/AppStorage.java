package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;

public interface AppStorage {

	BundleInfo getBundleById(String id);
		
	List<BundleInfo> getInstalledBundles();
	
	boolean updateBundle(BundleInfo bundleInfo);
	
	boolean deleteBundle(BundleInfo bundleInfo);
	
	boolean storeBundle(BundleInfo bundleInfo);
}
