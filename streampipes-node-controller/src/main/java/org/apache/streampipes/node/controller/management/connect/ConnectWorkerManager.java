/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.node.controller.management.connect;

import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.node.controller.management.connect.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectWorkerManager implements IConnectManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectWorkerManager.class.getCanonicalName());

    private static ConnectWorkerManager instance = null;

    private ConnectWorkerManager() {}

    public static ConnectWorkerManager getInstance() {
        if (instance == null) {
            synchronized (ConnectWorkerManager.class) {
                if (instance == null)
                    instance = new ConnectWorkerManager();
            }
        }
        return instance;
    }

    @Override
    public Response register(String username, ConnectWorkerContainer worker) {
        return new MasterRegistrationHandler(username, worker).handle();
    }

    @Override
    public <T extends AdapterDescription> String invoke(String username, T adapterDescription) {
        return new WorkerInteractionHandler<T>(username, adapterDescription, ConnectInteractionType.INVOKE).handle();
    }

    @Override
    public <T extends AdapterDescription> String detach(String username, T adapterDescription) {
        return new WorkerInteractionHandler<T>(username, adapterDescription, ConnectInteractionType.DETACH).handle();
    }

    @Override
    public GuessSchema guessSchema(String username, AdapterDescription adapterDescription) {
        return new WorkerGuessSchemaHandler(username, adapterDescription).handle();
    }

    @Override
    public String fetchConfigurations(String username, String appId, RuntimeOptionsRequest options) {
        return new WorkerFetchConfigurationsHandler(username, appId, options).handle();
    }

    @Override
    public byte[] fetchAssets(String username, String appId, String assetType, String subroute) {
        return new WorkerFetchAssetsHandler(username, appId, assetType, subroute).handle();
    }
}
