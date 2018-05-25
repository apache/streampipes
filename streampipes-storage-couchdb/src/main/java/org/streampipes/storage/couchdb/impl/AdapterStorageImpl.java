package org.streampipes.storage.couchdb.impl;


import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;
import org.streampipes.storage.api.IAdapterStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.dao.DbCommand;
import org.streampipes.storage.couchdb.dao.FindCommand;
import org.streampipes.storage.couchdb.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
        update(adapter);
    }

    @Override
    public AdapterDescription getAdapter(String adapterId) {
        InputStream in = couchDbClientSupplier.get().find(adapterId);

        // TODO find better solution
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(in, writer, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String theString = writer.toString();
//        System.out.println(theString);
        if (theString.contains("dataSet")) {
            DbCommand<Optional<AdapterSetDescription>, AdapterSetDescription> cmd = new FindCommand<AdapterSetDescription>(couchDbClientSupplier, adapterId, AdapterSetDescription.class);
            return cmd.execute().get();
        } else {
             DbCommand<Optional<AdapterStreamDescription>, AdapterStreamDescription> cmd = new FindCommand<AdapterStreamDescription>(couchDbClientSupplier, adapterId, AdapterStreamDescription.class);
            return cmd.execute().get();
        }
    }

    @Override
    public void deleteAdapter(String adapterId) {
        delete(adapterId);
    }
}
