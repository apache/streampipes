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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.connect.ConnectInteractionType;
import org.apache.streampipes.node.controller.management.connect.handler.utils.ConnectEndpointUtils;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerInteractionHandler<T extends AdapterDescription> implements IHandler<String> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerInteractionHandler.class.getCanonicalName());

    private static final String INVOKE_ROUTE = "/invoke";
    private static final String STOP_ROUTE ="/stop";

    private final String username;
    private final T adapterDescription;
    private final ConnectInteractionType connectType;

    public WorkerInteractionHandler(String username, T adapterDescription, ConnectInteractionType connectType) {
        this.username = username;
        this.adapterDescription = adapterDescription;
        this.connectType = connectType;
    }

    @Override
    public String handle() {
        switch(connectType) {
            case INVOKE:
                return invoke();
            case DETACH:
                return detach();
            default:
                throw new SpRuntimeException("Unsupported interaction scheme: " + connectType);
        }
    }

    private String invoke() {
        LOG.info("Invoke adapter: appId=" + adapterDescription.getAppId() + ", name=" + adapterDescription.getName());

        String url;
        if (adapterDescription instanceof AdapterStreamDescription) {
            url = ConnectEndpointUtils.generateAdapterStreamEndpoint(username, adapterDescription, INVOKE_ROUTE);
            return HttpUtils.post(url, adapterDescription, String.class);

        } else if (adapterDescription instanceof AdapterSetDescription) {
            url = ConnectEndpointUtils.generateAdapterSetEndpoint(username, adapterDescription, INVOKE_ROUTE);
            return HttpUtils.post(url, adapterDescription, String.class);
        }
        throw new SpRuntimeException("Could not invoke adapter: " + adapterDescription.getAppId());
    }

    private String detach() {
        LOG.info("Stop adapter: appId=" + adapterDescription.getAppId() + ", name=" + adapterDescription.getName());
        String url;
        if (adapterDescription instanceof AdapterStreamDescription) {
            url = ConnectEndpointUtils.generateAdapterStreamEndpoint(username, adapterDescription, STOP_ROUTE);
            return HttpUtils.post(url, adapterDescription, String.class);

        } else if (adapterDescription instanceof AdapterSetDescription) {
            url = ConnectEndpointUtils.generateAdapterSetEndpoint(username, adapterDescription, STOP_ROUTE);
            return HttpUtils.post(url, adapterDescription, String.class);
        }
        throw new SpRuntimeException("Could not stop adapter: " + adapterDescription.getAppId());
    }
}
