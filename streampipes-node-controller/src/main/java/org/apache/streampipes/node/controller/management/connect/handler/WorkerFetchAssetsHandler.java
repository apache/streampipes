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
package org.apache.streampipes.node.controller.management.connect.handler;

import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.connect.handler.utils.ConnectEndpointUtils;
import org.apache.streampipes.node.controller.utils.HttpUtils;

public class WorkerFetchAssetsHandler implements IHandler<byte[]> {

    private final String username;
    private final String appId;
    private final String assetType;
    private final String subroute;

    public WorkerFetchAssetsHandler(String username, String appId, String assetType, String subroute) {
        this.username = username;
        this.appId = appId;
        this.assetType = assetType;
        this.subroute = subroute;
    }

    @Override
    public byte[] handle() {
        String url = null;
        if ("adapter".equals(assetType)) {
            url = ConnectEndpointUtils.generateFetchAdapterAssetsEndpoint(username, appId, subroute);
        } else if ("protocol".equals(assetType)) {
            url = ConnectEndpointUtils.generateFetchProtocolAssetsEndpoint(username, appId, subroute);
        }

        return HttpUtils.get(url, byte[].class);
    }
}
