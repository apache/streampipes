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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.pe.PipelineElementLifeCycleState;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineElementInteractionHandler implements IHandler<Response> {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineElementInteractionHandler.class.getCanonicalName());
    private static final String SLASH = "/";
    private static final long RETRY_INTERVAL_MS = 5000;

    private final InvocableStreamPipesEntity graph;
    private final PipelineElementLifeCycleState type;
    private final String runningInstanceId;

    public PipelineElementInteractionHandler(InvocableStreamPipesEntity graph, PipelineElementLifeCycleState type) {
        this(graph, type, "");
    }

    public PipelineElementInteractionHandler(InvocableStreamPipesEntity graph, PipelineElementLifeCycleState type, String runningInstanceId) {
        this.graph = graph;
        this.type = type;
        this.runningInstanceId = runningInstanceId;
    }

    @Override
    public Response handle() {
        switch(type) {
            case INVOKE:
                return invoke();
            case DETACH:
                return detach();
            default:
                throw new SpRuntimeException("Life cycle step not supported" + type);
        }
    }

    private Response invoke() {
        Response response = new Response();
        String url = graph.getBelongsTo();

        LOG.info("Trying to invoke pipeline element: {}", url);
        boolean connected = false;
        while (!connected) {

            response = HttpUtils.post(url, graph);
            connected = response.isSuccess();

            if (!connected) {
                LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Failed to invoke pipeline element: " + url);
                }
            }
        }
        LOG.info("Successfully invoked pipeline element {}", url);
        return response;
    }

    private Response detach() {
        Response response = new Response();
        String url = graph.getBelongsTo() + SLASH + runningInstanceId;

        LOG.info("Trying to detach pipeline element: {}", url);
        boolean connected = false;
        while (!connected) {

            response = HttpUtils.delete(url);
            connected = response.isSuccess();

            if (!connected) {
                LOG.debug("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Failed to detach pipeline element: " + url, e);
                }
            }
        }
        LOG.info("Successfully detached pipeline element {}", url);
        return response;
    }
}
