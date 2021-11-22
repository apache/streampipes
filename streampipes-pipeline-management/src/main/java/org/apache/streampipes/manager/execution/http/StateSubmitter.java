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
package org.apache.streampipes.manager.execution.http;

import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StateSubmitter {
    protected final static Logger LOG = LoggerFactory.getLogger(StateSubmitter.class);

    private static final Integer CONNECT_TIMEOUT = 10000;

    private final String pipelineId;
    private final String pipelineName;
    private final InvocableStreamPipesEntity entity;
    private final String elementState;

    public StateSubmitter( String pipelineId, String pipelineName, InvocableStreamPipesEntity entity,
                                     String elementState) {
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.elementState = elementState;
        this.entity = entity;
    }

    public PipelineElementStatus setElementState(){
        String endpoint = new StateEndpointUrlGenerator(entity).generateStateEndpoint();
        LOG.info("Setting state of pipeline element: " + endpoint);

        try {
            Response httpResp = Request
                    .Put(endpoint)
                    .bodyString(elementState, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return handleResponse(endpoint, httpResp);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new PipelineElementStatus(endpoint, entity.getName(),
                    false, e.getMessage());
        }
    }

    public PipelineElementStatus getElementState(){
        String endpoint = new StateEndpointUrlGenerator(entity).generateStateEndpoint();
        LOG.info("Getting state of pipeline element: " + endpoint);

        try {
            Response httpResp = Request
                    .Get(endpoint)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return handleResponse(endpoint, httpResp);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new PipelineElementStatus(endpoint, entity.getName(),
                    false, e.getMessage());
        }
    }


    private PipelineElementStatus handleResponse(String endpoint, Response httpResp) throws JsonSyntaxException,
            IOException {
        String resp = httpResp.returnContent().asString();
        org.apache.streampipes.model.Response streamPipesResp = JacksonSerializer
                .getObjectMapper()
                .readValue(resp, org.apache.streampipes.model.Response.class);

        return new PipelineElementStatus(endpoint,
                entity.getName(),
                streamPipesResp.isSuccess(),
                streamPipesResp.getOptionalMessage());
    }

}
