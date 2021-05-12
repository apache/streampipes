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

public interface IConnectManager {
    Response register(String username, ConnectWorkerContainer worker);
    <T extends AdapterDescription> String invoke(String username, T adapterDescription);
    <T extends AdapterDescription> String detach(String username, T adapterDescription);
    GuessSchema guessSchema(String username, AdapterDescription adapterDescription);
    String fetchConfigurations(String username, String appId, RuntimeOptionsRequest options);
    byte[] fetchAssets(String username, String appId, String assetType, String subroute);
}
