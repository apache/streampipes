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
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReconfigurationSubmitter {
    protected final static Logger LOG = LoggerFactory.getLogger(ReconfigurationSubmitter.class);

    private static final Integer CONNECT_TIMEOUT = 10000;

    private final String pipelineId;
    private final String pipelineName;
    private final PipelineElementReconfigurationEntity reconfigurationEntity;

    public ReconfigurationSubmitter( String pipelineId, String pipelineName,
                                    PipelineElementReconfigurationEntity reconfigurationEntity) {
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.reconfigurationEntity = reconfigurationEntity;
    }

    public PipelineOperationStatus reconfigure() {
        PipelineOperationStatus status = initPipelineOperationStatus();

        reconfigurePipelineElements(status);

        return verifyPipelineOperationStatus(
                status,
                "Successfully reconfigured pipeline " + pipelineName,
                "Could not reconfigure pipeline " + pipelineName,
                true);
    }

    private void reconfigurePipelineElements(PipelineOperationStatus status) {
        status.addPipelineElementStatus(submit());
    }

    private PipelineElementStatus submit() {
        String endpoint = new ReconfigurationEndpointUrlGenerator(reconfigurationEntity).generateEndpoint();
        LOG.info("Reconfigure pipeline element: " + endpoint);
        try {
            String json = toJson();
            Response httpResp = Request
                    .Post(endpoint)
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return handleResponse(endpoint, httpResp);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new PipelineElementStatus(endpoint, reconfigurationEntity.getPipelineElementName(),
                    false, e.getMessage());
        }
    }

    private void rollback(PipelineOperationStatus status) {
        //TODO: implement
    }

    private PipelineElementStatus handleResponse(String endpoint, Response httpResp) throws JsonSyntaxException,
            IOException {
        String resp = httpResp.returnContent().asString();
        org.apache.streampipes.model.Response streamPipesResp = JacksonSerializer
                .getObjectMapper()
                .readValue(resp, org.apache.streampipes.model.Response.class);

        return new PipelineElementStatus(endpoint,
                reconfigurationEntity.getPipelineElementName(),
                streamPipesResp.isSuccess(),
                streamPipesResp.getOptionalMessage());
    }


    private PipelineOperationStatus verifyPipelineOperationStatus(PipelineOperationStatus status, String successMessage,
                                                                    String errorMessage, boolean rollbackIfFailed) {
        status.setSuccess(status.getElementStatus().stream()
                .allMatch(PipelineElementStatus::isSuccess));

        if (status.isSuccess()) {
            status.setTitle(successMessage);
        } else {
            if (rollbackIfFailed) {
                LOG.info("Could not reconfigure pipeline element, initializing rollback...");
                rollback(status);
            }
            status.setTitle(errorMessage);
        }
        return status;
    }

    private PipelineOperationStatus initPipelineOperationStatus() {
        PipelineOperationStatus status = new PipelineOperationStatus();
        status.setPipelineId(pipelineId);
        status.setPipelineName(pipelineName);
        status.setSuccess(true);
        return status;
    }

    private String toJson() throws Exception {
        return JacksonSerializer.getObjectMapper().writeValueAsString(reconfigurationEntity);
    }
}
