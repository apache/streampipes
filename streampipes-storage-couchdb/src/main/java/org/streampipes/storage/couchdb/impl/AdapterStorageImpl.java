package org.streampipes.storage.couchdb.impl;


import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.storage.api.IAdapterStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdapterStorageImpl extends AbstractDao<AdapterDescription> implements IAdapterStorage {

    Logger LOG = LoggerFactory.getLogger(AdapterStorageImpl.class);

    private static final String SYSTEM_USER = "system";

    public AdapterStorageImpl() {
        super(Utils::getCouchDbAdapterClient, AdapterDescription.class);
    }

    @Override
    public List<AdapterDescription> getAllAdapters() {
        return findAll();
    }

    @Override
    public void storeAdapter(AdapterDescription adapter) {
        persist(adapter);
    }

    @Override
    public void updateAdapter(AdapterDescription adapter) {
        update(adapter);
    }

    @Override
    public AdapterDescription getAdapter(String adapterId) {
        return findWithNullIfEmpty(adapterId);
    }

    @Override
    public void deleteAdapter(String adapterId) {
        delete(adapterId);
    }
}
