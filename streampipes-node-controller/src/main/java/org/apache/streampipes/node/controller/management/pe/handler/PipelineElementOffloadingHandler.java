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
package org.apache.streampipes.node.controller.management.pe.handler;

import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.utils.HttpUtils;


public class PipelineElementOffloadingHandler implements IHandler<Response> {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String BASE_ROUTE = "streampipes-backend/api/v2/users/admin@streampipes.org";
    private static final String OFFLOADING_ROUTE = BASE_ROUTE + "/pipelines/offload";

    private final InvocableStreamPipesEntity graph;

    public PipelineElementOffloadingHandler(InvocableStreamPipesEntity graph) {
        this.graph = graph;
    }

    @Override
    public Response handle() {
        String url = generatePipelineManagementOffloadEndpoint();
        return HttpUtils.post(url, graph);
    }

    private String generatePipelineManagementOffloadEndpoint() {
        return HTTP_PROTOCOL
                + NodeConfiguration.getBackendHost()
                + COLON
                + NodeConfiguration.getBackendPort()
                + SLASH
                + OFFLOADING_ROUTE;
    }
}
