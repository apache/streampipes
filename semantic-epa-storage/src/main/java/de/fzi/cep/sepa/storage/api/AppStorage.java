package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;

public interface AppStorage {

	public BundleInfo getBundleById(String id);
		
	public List<BundleInfo> getInstalledBundles();
	
	public boolean updateBundle(BundleInfo bundleInfo);
	
	public boolean deleteBundle(BundleInfo bundleInfo);
	
	public boolean storeBundle(BundleInfo bundleInfo);
}
