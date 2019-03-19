/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.couchdb.impl;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.storage.api.IAdapterTemplateStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.dao.DbCommand;
import org.streampipes.storage.couchdb.dao.FindCommand;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.Optional;

public class AdapterTemplateStorageImpl extends AbstractDao<AdapterDescription> implements IAdapterTemplateStorage {

    Logger LOG = LoggerFactory.getLogger(AdapterStorageImpl.class);

    private static final String SYSTEM_USER = "system";

    public AdapterTemplateStorageImpl() {
        super(Utils::getCouchDbAdapterTemplateClient, AdapterDescription.class);
    }

    @Override
    public List<AdapterDescription> getAllAdapterTemplates() {
        return findAll();
    }

    @Override
    public void storeAdapterTemplate(AdapterDescription adapter) {
        persist(adapter);
    }

    @Override
    public void updateAdapterTemplate(AdapterDescription adapter) {
        couchDbClientSupplier.get().
        update(adapter);
    }

    @Override
    public AdapterDescription getAdapterTemplate(String adapterId) {
        DbCommand<Optional<AdapterDescription>, AdapterDescription> cmd = new FindCommand<>(couchDbClientSupplier, adapterId, AdapterDescription.class);
        return cmd.execute().get();
    }

    @Override
    public void deleteAdapterTemplate(String adapterId) {
        AdapterDescription adapterDescription = getAdapterTemplate(adapterId);
        couchDbClientSupplier.get().remove(adapterDescription.getId(), adapterDescription.getRev());
    }
}
