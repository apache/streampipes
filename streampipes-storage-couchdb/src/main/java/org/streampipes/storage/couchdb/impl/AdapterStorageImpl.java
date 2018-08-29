package org.streampipes.storage.couchdb.impl;


import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.storage.api.IAdapterStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.dao.DbCommand;
import org.streampipes.storage.couchdb.dao.FindCommand;
import org.streampipes.storage.couchdb.utils.Utils;

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

        DbCommand<Optional<AdapterDescription>, AdapterDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, AdapterDescription.class);
        return cmd.execute().get();


//         TODO find better solution
//        StringWriter writer = new StringWriter();
//        try {
//            IOUtils.copy(in, writer, Charsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String theString = writer.toString();
//
//
//        return GsonSerializer.getGson().fromJson(theString, AdapterDescription.class);

//        System.out.println(theString);
//        if (theString.contains("dataSet")) {
//            DbCommand<Optional<AdapterSetDescription>, AdapterSetDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, AdapterSetDescription.class);
//            return cmd.execute().get();
//        } else {
//             DbCommand<Optional<AdapterStreamDescription>, AdapterStreamDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, AdapterStreamDescription.class);
//            return cmd.execute().get();
//        }
//        if (theString.contains("GenericAdapterStreamDescription")) {
//            DbCommand<Optional<GenericAdapterStreamDescription>, GenericAdapterStreamDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, GenericAdapterStreamDescription.class);
//            return cmd.execute().get();
//        } else  if (theString.contains("GenericAdapterSetDescription")) {
//            DbCommand<Optional<GenericAdapterSetDescription>, GenericAdapterSetDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, GenericAdapterSetDescription.class);
//            return cmd.execute().get();
//        } if (theString.contains("SpecificAdapterStreamDescription")) {
//            DbCommand<Optional<SpecificAdapterStreamDescription>, SpecificAdapterStreamDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, SpecificAdapterStreamDescription.class);
//            return cmd.execute().get();
//        } else if (theString.contains("SpecificAdapterSetDescription")) {
//             DbCommand<Optional<SpecificAdapterSetDescription>, SpecificAdapterSetDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, SpecificAdapterSetDescription.class);
//            return cmd.execute().get();
//        }

//        return null;
    }

    @Override
    public void deleteAdapter(String adapterId) {
        delete(adapterId);
    }
}
