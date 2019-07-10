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

package org.streampipes.connect.management.master;

import org.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;

public class WorkerAdministrationManagement {

    public void register(ConnectWorkerContainer connectWorker) {
        // Check if already registered
        ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();

        ConnectWorkerContainer inStorage = connectionWorkerContainerStorage.getConnectWorkerContainer(connectWorker.getId());

        // IF NOT REGISTERED
        // Store Connect Worker in DB
        if (inStorage == null) {
            connectionWorkerContainerStorage.storeConnectWorkerContainer(connectWorker);

        } else {
            // IF REGISTERED
            // Start all adapters on worker
        }

    }
}
