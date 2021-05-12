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

import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.utils.HttpUtils;

public class WorkerFetchConfigurationsHandler implements IHandler<String> {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";

    private static final String CONNECT_WORKER_HOST = "streampipes-extensions";
    private static final int CONNECT_WORKER_PORT = 8090;
    private static final String CONNECT_WORKER_BASE_ROUTE = "/api/v1/{username}/worker";
    private static final String RESOLVABLE_ROUTE = "/resolvable/{id}/configurations";

    private final String username;
    private final String appId;
    private final RuntimeOptionsRequest options;

    public WorkerFetchConfigurationsHandler(String username, String appId, RuntimeOptionsRequest options) {
        this.username = username;
        this.appId = appId;
        this.options = options;
    }

    @Override
    public String handle() {
        String url = generateFetchConfigurationsEndpoint();
        String body = HttpUtils.serialize(options);

        return HttpUtils.post(url, body);
    }

    // Helpers

    private String generateFetchConfigurationsEndpoint() {
        return workerUrl()
                + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                + RESOLVABLE_ROUTE.replace("{id}", appId);
    }


    private String workerUrl() {
        if("true".equals(System.getenv("SP_DEBUG"))) {
            return HTTP_PROTOCOL + "localhost" + COLON + "7024";
        }
        return HTTP_PROTOCOL + CONNECT_WORKER_HOST + COLON + CONNECT_WORKER_PORT;
    }
}
