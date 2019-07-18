package org.streampipes.storage.couchdb.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.storage.api.IAdapterStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.dao.DbCommand;
import org.streampipes.storage.couchdb.dao.FindCommand;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.Optional;

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
        couchDbClientSupplier.get().
        update(adapter);
    }

    @Override
    public AdapterDescription getAdapter(String adapterId) {
        DbCommand<Optional<AdapterDescription>, AdapterDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, AdapterDescription.class);
        return cmd.execute().get();
    }

    @Override
    public void deleteAdapter(String adapterId) {

        AdapterDescription adapterDescription = getAdapter(adapterId);
        couchDbClientSupplier.get().remove(adapterDescription.getId(), adapterDescription.getRev());

    }
}
