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

import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelay;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public abstract class ElementSubmitter {
    protected final static Logger LOG = LoggerFactory.getLogger(ElementSubmitter.class);

    protected final String pipelineId;
    protected final String pipelineName;
    protected final List<InvocableStreamPipesEntity> graphs;
    protected final List<SpDataSet> dataSets;
    protected final List<SpDataStreamRelayContainer> relays;

    public ElementSubmitter(String pipelineId, String pipelineName, List<InvocableStreamPipesEntity> graphs,
                            List<SpDataSet> dataSets, List<SpDataStreamRelayContainer> relays) {
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.graphs = graphs;
        this.dataSets = dataSets;
        this.relays = relays;
    }

    protected abstract PipelineOperationStatus invokePipelineElementsAndRelays();

    protected abstract PipelineOperationStatus detachPipelineElementsAndRelays();

    protected abstract PipelineOperationStatus invokeRelaysOnMigration();

    protected abstract PipelineOperationStatus detachRelaysOnMigration();


    protected void invokePipelineElements(PipelineOperationStatus status) {
        graphs.forEach(graph ->
                status.addPipelineElementStatus(makeInvokeHttpRequest(new InvocableEntityUrlGenerator(graph), graph)));
    }

    protected void detachPipelineElements(PipelineOperationStatus status) {
        graphs.forEach(graph ->
                status.addPipelineElementStatus(makeDetachHttpRequest(new InvocableEntityUrlGenerator(graph), graph)));
    }

    protected void invokeDataSets(PipelineOperationStatus status) {
        dataSets.forEach(dataset ->
                status.addPipelineElementStatus(makeInvokeHttpRequest(new DataSetEntityUrlGenerator(dataset), dataset)));
    }

    protected void detachDataSets(PipelineOperationStatus status) {
        dataSets.forEach(dataset ->
                status.addPipelineElementStatus(makeDetachHttpRequest(new DataSetEntityUrlGenerator(dataset), dataset)));
    }

    protected void invokeRelays(PipelineOperationStatus status) {
        relays.forEach(relay ->
                status.addPipelineElementStatus(makeInvokeHttpRequest(new StreamRelayEndpointUrlGenerator(relay), relay)));
    }

    protected void detachRelays(PipelineOperationStatus status) {
        relays.forEach(relay ->
                status.addPipelineElementStatus(makeDetachHttpRequest(new StreamRelayEndpointUrlGenerator(relay), relay)));
    }


    protected PipelineElementStatus makeInvokeHttpRequest(EndpointUrlGenerator<?> urlGenerator,
                                                        NamedStreamPipesEntity entity) {
        if (entity instanceof SpDataStreamRelay) {
            // data stream relays
            return new HttpRequestBuilder(entity, urlGenerator.generateRelayEndpoint()).invoke();
        } else {
            // data sets, data processors, data sinks
            return new HttpRequestBuilder(entity, urlGenerator.generateInvokeEndpoint()).invoke();
        }
    }

    protected PipelineElementStatus makeDetachHttpRequest(EndpointUrlGenerator<?> urlGenerator,
                                                        NamedStreamPipesEntity entity) {
        if (entity instanceof SpDataStreamRelay) {
            // data stream relays
            return new HttpRequestBuilder(entity, urlGenerator.generateRelayEndpoint()).detach();
        } else {
            // data sets, data processors, data sinks
            return new HttpRequestBuilder(entity, urlGenerator.generateDetachEndpoint()).detach();
        }
    }

    protected PipelineOperationStatus verifyPipelineOperationStatus(PipelineOperationStatus status, String successMessage,
                                                                  String errorMessage, boolean rollbackIfFailed) {
        status.setSuccess(status.getElementStatus().stream()
                .allMatch(PipelineElementStatus::isSuccess));

        if (status.isSuccess()) {
            status.setTitle(successMessage);
        } else {
            if (rollbackIfFailed) {
                LOG.info("Could not start pipeline, initializing rollback...");
                rollbackInvokedEntities(status);
            }
            status.setTitle(errorMessage);
        }
        return status;
    }

    private void rollbackInvokedEntities(PipelineOperationStatus status) {
        for (PipelineElementStatus s : status.getElementStatus()) {
            if (s.isSuccess()) {
                Optional<InvocableStreamPipesEntity> graphs = compareAndFindGraphs(s.getElementId());
                Optional<SpDataStreamRelayContainer> relays = compareAndFindRelays(s.getElementId());
                graphs.ifPresent(graph -> {
                    LOG.info("Rolling back element " + s.getElementId());
                    makeDetachHttpRequest(new InvocableEntityUrlGenerator(graph), graph);
                });
                relays.ifPresent(relay -> {
                    LOG.info("Rolling back element " + s.getElementId());
                    makeDetachHttpRequest(new StreamRelayEndpointUrlGenerator(relay), relay);
                });
            }
        }
    }

    // Helper methods

    protected PipelineOperationStatus initPipelineOperationStatus() {
        PipelineOperationStatus status = new PipelineOperationStatus();
        status.setPipelineId(pipelineId);
        status.setPipelineName(pipelineName);
        return status;
    }

    // Old: filter not working. leads to comparing endpoint of primary pipeline element with the one of nodectl
    // because endpoint in PipelineOperationStatus set in @{HttpRequestBuilder} used nodectl endpoint
    @Deprecated
    private Optional<InvocableStreamPipesEntity> findGraphs(String elementId) {
        return graphs.stream().filter(i -> i.getBelongsTo().equals(elementId)).findFirst();
    }

    private Optional<InvocableStreamPipesEntity> compareAndFindGraphs(String elementId) {
        return graphs.stream().filter(graph -> matchingEndpoints(graph, elementId)).findFirst();
    }

    private Optional<SpDataStreamRelayContainer> compareAndFindRelays(String elementId) {
        return relays.stream().filter(relay -> matchingEndpoints(relay, elementId)).findFirst();
    }

    private boolean matchingEndpoints(NamedStreamPipesEntity entity, String endpoint) {
        boolean matching = false;
        if (entity instanceof InvocableStreamPipesEntity) {
            matching = new InvocableEntityUrlGenerator((InvocableStreamPipesEntity) entity)
                    .generateInvokeEndpoint().equals(endpoint);
        } else if (entity instanceof SpDataStreamRelayContainer) {
            matching = new StreamRelayEndpointUrlGenerator((SpDataStreamRelayContainer) entity)
                    .generateInvokeEndpoint().equals(endpoint);
        }
        return matching;
    }

    protected boolean allInvocableEntitiesRunning(PipelineOperationStatus status) {
        return status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess);
    }

    protected boolean relaysExist() {
        return relays.stream().anyMatch(s -> s.getOutputStreamRelays().size() > 0);
    }
}
