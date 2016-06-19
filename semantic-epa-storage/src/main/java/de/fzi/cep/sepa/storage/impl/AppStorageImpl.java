package de.fzi.cep.sepa.storage.impl;

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;
import de.fzi.cep.sepa.storage.api.AppStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class AppStorageImpl implements AppStorage {

	@Override
	public BundleInfo getBundleById(String bundleId) {
		CouchDbClient dbClient = Utils.getCouchDbAppStorageClient();
        try {
            BundleInfo bundleInfo = dbClient.find(BundleInfo.class, bundleId);
            dbClient.shutdown();
            return bundleInfo;
        } catch (NoDocumentException e) {
            return null;
        }
	}

	@Override
	public List<BundleInfo> getInstalledBundles() {
		 CouchDbClient dbClient = Utils.getCouchDbAppStorageClient();
    	 List<BundleInfo> bundles = dbClient.view("_all_docs")
    			  .includeDocs(true)
    			  .query(BundleInfo.class);
    	 
    	 return bundles;
	}

	@Override
	public boolean updateBundle(BundleInfo bundleInfo) {
		CouchDbClient dbClient = Utils.getCouchDbAppStorageClient();
        dbClient.update(bundleInfo);
        dbClient.shutdown();
        return true;
	}

	@Override
	public boolean deleteBundle(BundleInfo bundleInfo) {
		 CouchDbClient dbClientPipeline = Utils.getCouchDbAppStorageClient();
	        try {
	            BundleInfo removeBundleInfo = dbClientPipeline.find(BundleInfo.class, bundleInfo.getId());
	            dbClientPipeline.remove(removeBundleInfo);
	            dbClientPipeline.shutdown();
	            return true;
	        } catch (NoDocumentException e) {
	            e.printStackTrace();
	            return false;
	        }
	}

	@Override
	public boolean storeBundle(BundleInfo bundleInfo) {
		CouchDbClient dbClient = Utils.getCouchDbAppStorageClient();
        dbClient.save(bundleInfo);
        dbClient.shutdown();
        return true;
	}

}
