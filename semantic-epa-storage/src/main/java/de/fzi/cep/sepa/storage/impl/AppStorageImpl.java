package de.fzi.cep.sepa.storage.impl;

import java.util.List;
import java.util.Optional;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;
import de.fzi.cep.sepa.storage.api.AppStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class AppStorageImpl extends Storage<BundleInfo> implements AppStorage {

    public AppStorageImpl() {
        super(BundleInfo.class);
    }

    @Override
    public BundleInfo getBundleById(String bundleId) {
        return getWithNullIfEmpty(bundleId);
    }

    @Override
    public List<BundleInfo> getInstalledBundles() {
        return getAll();
    }

    @Override
    public boolean updateBundle(BundleInfo bundleInfo) {
        update(bundleInfo);
        return true;
    }

    @Override
    public boolean deleteBundle(BundleInfo bundleInfo) {
        try {
            CouchDbClient dbClient = getCouchDbClient();
            BundleInfo removeBundleInfo = dbClient.find(BundleInfo.class, bundleInfo.getId());
            dbClient.remove(removeBundleInfo);
            dbClient.shutdown();
            return true;
        } catch (NoDocumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean storeBundle(BundleInfo bundleInfo) {
        add(bundleInfo);
        return true;
    }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbAppStorageClient();
    }
}
