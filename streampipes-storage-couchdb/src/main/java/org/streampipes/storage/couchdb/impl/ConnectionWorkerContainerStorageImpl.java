/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.streampipes.storage.api.IConnectWorkerContainerStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.dao.DbCommand;
import org.streampipes.storage.couchdb.dao.FindCommand;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.Optional;

public class ConnectionWorkerContainerStorageImpl extends AbstractDao<ConnectWorkerContainer> implements IConnectWorkerContainerStorage {

    Logger LOG = LoggerFactory.getLogger(ConnectionWorkerContainerStorageImpl.class);


    public ConnectionWorkerContainerStorageImpl() {
        super(Utils::getCouchDbAdapterTemplateClient, ConnectWorkerContainer.class);
    }

    @Override
    public List<ConnectWorkerContainer> getAllConnectWorkerContainers() {
        return findAll();
    }

    @Override
    public void storeConnectWorkerContainer(ConnectWorkerContainer connectWorkerContainer) {
        persist(connectWorkerContainer);

    }

    @Override
    public void updateConnectWorkerContainer(ConnectWorkerContainer connectWorkerContainer) {
        couchDbClientSupplier.get().
                update(connectWorkerContainer);
    }

    @Override
    public ConnectWorkerContainer getConnectWorkerContainer(String connectWorkerContainerId) {
        DbCommand<Optional<ConnectWorkerContainer>, ConnectWorkerContainer> cmd = new FindCommand<>(couchDbClientSupplier, connectWorkerContainerId, ConnectWorkerContainer.class);
        return cmd.execute().get();
    }

    @Override
    public void deleteConnectWorkerContainer(String connectWorkerContainerId) {
        ConnectWorkerContainer connectWorkerContainer = getConnectWorkerContainer(connectWorkerContainerId);
        couchDbClientSupplier.get().remove(connectWorkerContainer.getId(), connectWorkerContainer.getRev());
    }
}

