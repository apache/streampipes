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
package org.apache.streampipes.manager.execution.pipeline;

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.manager.matching.InvocationGraphBuilder;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.*;
import java.util.stream.Collectors;

public class PipelineMigrationExecutor extends AbstractPipelineExecutor {

    /**
     * Old pipeline before migration
     */
    private final Pipeline pipelineBeforeMigration;

    /**
     * Pipeline element to be migrated. Contains pair of source and target element description
     */
    private final PipelineElementMigrationEntity migrationEntity;

    /**
     * Predecessors of the pipeline element to be migrated in the migration pipeline
     */
    private final List<NamedStreamPipesEntity> predecessorsAfterMigration;

    /**
     * Predecessors of the pipeline element to be migrated in the old pipeline before migration
     */
    private final List<NamedStreamPipesEntity> predecessorsBeforeMigration;

    /**
     * Collection of relays that were created in the migration process needing to be stored
     */
    private final List<SpDataStreamRelayContainer> relaysToBePersisted;

    /**
     * Collection of relays that were removed in the migration process needing to be deleted from persistent storage.
     */
    private final List<SpDataStreamRelayContainer> relaysToBeDeleted;


    public PipelineMigrationExecutor(Pipeline pipeline, Pipeline pipelineBeforeMigration,
                                     PipelineElementMigrationEntity migrationEntity,
                                     boolean visualize, boolean storeStatus, boolean monitor) {
        super(pipeline, visualize, storeStatus, monitor);
        this.pipelineBeforeMigration = pipelineBeforeMigration;
        this.migrationEntity = migrationEntity;
        this.predecessorsAfterMigration = new ArrayList<>();
        this.predecessorsBeforeMigration = new ArrayList<>();
        this.relaysToBePersisted = new ArrayList<>();
        this.relaysToBeDeleted = new ArrayList<>();
    }

    // TODO: refactor!
    public PipelineOperationStatus migratePipelineElement() {

        PipelineOperationStatus status = initPipelineOperationStatus();

        // 1. start new element
        // 2. stop relay to origin element
        // 3. start relay to new element
        // 4. stop origin element
        // 5. stop origin relay

        prepareMigration();

        // Start target pipeline elements and relays on new target node
        PipelineOperationStatus statusStartTarget = startTargetPipelineElementsAndRelays(status);
        if(!statusStartTarget.isSuccess()){
            //Target PE could not be started; nothing to roll back
            return status;
        }

        // Stop relays from origin predecessor
        PipelineOperationStatus statusStopRelays = stopRelaysFromPredecessorsBeforeMigration(status);
        if(!statusStopRelays.isSuccess()){
            rollbackToPreMigrationStepOne(statusStopRelays, status);
            return status;
        }

        // Start relays to target after migration
        PipelineOperationStatus statusStartRelays = startRelaysFromPredecessorsAfterMigration(status);
        if(!statusStartRelays.isSuccess()){
            rollbackToPreMigrationStepTwo(statusStartRelays, status);
            return status;
        }

        //Stop origin and associated relay
        PipelineOperationStatus statusStopOrigin = stopOriginPipelineElementsAndRelays(status);
        if(!statusStopOrigin.isSuccess()){
            rollbackToPreMigrationStepThree(status);
            return status;
        }

        List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
        graphs.addAll(pipeline.getActions());
        graphs.addAll(pipeline.getSepas());

        List<SpDataSet> dataSets = findDataSets();

        // store new pipeline and relays
        storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);
        storeDataStreamRelayContainer(relaysToBePersisted);
        deleteDataStreamRelayContainer(relaysToBeDeleted);

        // set global status
        status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));

        return status;
    }

    private void prepareMigration() {
        //Purge existing relays
        purgeExistingRelays();

        //Generate new relays
        PipelineGraph pipelineGraphAfterMigration = new PipelineGraphBuilder(pipeline).buildGraph();
        buildGraphWithRelays(pipelineGraphAfterMigration);

        //Get predecessors
        PipelineGraph pipelineGraphBeforeMigration = new PipelineGraphBuilder(pipelineBeforeMigration).buildGraph();
        findPredecessorsInMigrationPipeline(pipelineGraphAfterMigration);

        //Find counterpart for predecessors in currentPipeline
        findAndComparePredecessorsInCurrentPipeline(pipelineGraphBeforeMigration);
    }

    private void rollbackToPreMigrationStepOne(PipelineOperationStatus statusStopRelays,
                                               PipelineOperationStatus status) {
        // Stop target pipeline element and relays on new target node
        PipelineOperationStatus statusTargetRollback = rollbackTargetPipelineElementsAndRelays();

        // Restart relays before migration attempt
        // extract unique running instance ids of original relays
        Set<String> relayIdsToRollback = extractUniqueRelayIds(statusStopRelays);
        PipelineOperationStatus statusRelaysRollback = rollbackToOriginRelaysByInvoke(relayIdsToRollback);

        // Add status to global migration status
        updateMigrationStatus(statusTargetRollback, status);
        updateMigrationStatus(statusRelaysRollback, status);
    }

    private void rollbackToPreMigrationStepTwo(PipelineOperationStatus statusStartRelays,
                                               PipelineOperationStatus status) {
        //Rollback target PE, stopped relays and all successfully started relays
        PipelineOperationStatus statusTargetRollback = rollbackTargetPipelineElementsAndRelays();

        // Restart relays to origin from predecessors before migration
        PipelineOperationStatus statusRelaysInvokeRollback = startRelays(findRelays(predecessorsBeforeMigration,
                migrationEntity.getSourceElement()));

        // Stop relays that were started due to migration attempt
        // extract unique running instance ids of original relays
        Set<String> relayIdsToRollback = extractUniqueRelayIds(statusStartRelays);
        PipelineOperationStatus statusRelaysDetachRollback = rollbackTargetRelaysByDetach(relayIdsToRollback);

        // Add status to global migration status
        updateMigrationStatus(statusTargetRollback, status);
        updateMigrationStatus(statusRelaysInvokeRollback, status);
        updateMigrationStatus(statusRelaysDetachRollback, status);
    }

    private void rollbackToPreMigrationStepThree(PipelineOperationStatus status) {
        //Rollback everything
        PipelineOperationStatus statusStopRelays = stopRelays(findRelays(predecessorsAfterMigration,
                migrationEntity.getTargetElement()));

        PipelineOperationStatus statusStartRelays = startRelays(findRelays(predecessorsBeforeMigration,
                migrationEntity.getSourceElement()));

        List<InvocableStreamPipesEntity> graphs = Collections.singletonList(migrationEntity.getTargetElement());
        List<SpDataStreamRelayContainer> relays = extractRelaysFromDataProcessor(graphs);

        PipelineOperationStatus statusStopTargetAndRelays = stopPipelineElementsAndRelays(graphs, relays);

        // Add status to global migration status
        updateMigrationStatus(statusStopRelays, status);
        updateMigrationStatus(statusStartRelays, status);
        updateMigrationStatus(statusStopTargetAndRelays, status);
    }


    private PipelineOperationStatus rollbackToOriginRelaysByInvoke(Set<String> relayIdsToRollback) {
        List<SpDataStreamRelayContainer> rollbackRelays = findRelaysAndFilterById(relayIdsToRollback,
                predecessorsBeforeMigration, migrationEntity.getSourceElement());

        return startRelays(rollbackRelays);
    }

    private PipelineOperationStatus rollbackTargetRelaysByDetach(Set<String> relayIdsToRollback) {
        List<SpDataStreamRelayContainer> rollbackRelays = findRelaysAndFilterById(relayIdsToRollback,
                predecessorsAfterMigration, migrationEntity.getTargetElement());

        return stopRelays(rollbackRelays);
    }

    private PipelineOperationStatus rollbackTargetPipelineElementsAndRelays() {
        List<InvocableStreamPipesEntity> graphs = Collections.singletonList(migrationEntity.getTargetElement());
        List<SpDataStreamRelayContainer> relays = extractRelaysFromDataProcessor(graphs);

        return new GraphSubmitter(pipeline.getPipelineId(),
                pipeline.getName(), graphs, new ArrayList<>(), relays).detachPipelineElementsAndRelays();
    }

    private PipelineOperationStatus startRelaysFromPredecessorsAfterMigration(PipelineOperationStatus status) {
        List<SpDataStreamRelayContainer> relays = findRelays(predecessorsAfterMigration,
                migrationEntity.getTargetElement());

        updateRelaysToBePersisted(relays);

        PipelineOperationStatus statusStartRelays = startRelays(relays);
        updateMigrationStatus(statusStartRelays, status);

        return statusStartRelays;
    }

    private PipelineOperationStatus stopRelaysFromPredecessorsBeforeMigration(PipelineOperationStatus status) {
        List<SpDataStreamRelayContainer> relays = findRelays(predecessorsBeforeMigration,
                migrationEntity.getSourceElement());

        updateRelaysToBeDeleted(relays);

        PipelineOperationStatus statusStopRelays = stopRelays(relays);
        updateMigrationStatus(statusStopRelays, status);

        return statusStopRelays;
    }

    private PipelineOperationStatus startTargetPipelineElementsAndRelays(PipelineOperationStatus status) {
        List<InvocableStreamPipesEntity> decryptedGraphs =
                decryptSecrets(Collections.singletonList(migrationEntity.getTargetElement()));
        List<SpDataStreamRelayContainer> relays = extractRelaysFromDataProcessor(decryptedGraphs);

        updateRelaysToBePersisted(relays);

        PipelineOperationStatus statusStartTarget = startPipelineElementsAndRelays(decryptedGraphs, relays);
        updateMigrationStatus(statusStartTarget, status);

        return statusStartTarget;
    }

    private PipelineOperationStatus stopOriginPipelineElementsAndRelays(PipelineOperationStatus status) {
        List<InvocableStreamPipesEntity> graphs = Collections.singletonList(migrationEntity.getSourceElement());
        List<SpDataStreamRelayContainer> relays = extractRelaysFromDataProcessor(graphs);

        updateRelaysToBeDeleted(relays);

        PipelineOperationStatus statusStopOrigin = stopPipelineElementsAndRelays(graphs, relays);
        updateMigrationStatus(statusStopOrigin, status);

        return statusStopOrigin;
    }

    // Helpers

    private void updateRelaysToBePersisted(List<SpDataStreamRelayContainer> relays) {
        relays.stream()
                .filter(r -> r.getOutputStreamRelays().size() > 0)
                .forEach(relaysToBePersisted::add);
    }

    private void updateRelaysToBeDeleted(List<SpDataStreamRelayContainer> relays) {
        relays.stream()
                .filter(r -> r.getOutputStreamRelays().size() > 0)
                .forEach(relaysToBeDeleted::add);
    }

    private List<SpDataStreamRelayContainer> findRelaysAndFilterById(Set<String> relayIdsToRollback,
                                                                     List<NamedStreamPipesEntity> predecessor,
                                                                     InvocableStreamPipesEntity target) {
        return findRelays(predecessor, target).stream()
                .filter(relay -> relayIdsToRollback.contains(relay.getRunningStreamRelayInstanceId()))
                .collect(Collectors.toList());
    }

    private void findAndComparePredecessorsInCurrentPipeline(PipelineGraph pipelineGraphBeforeMigration) {
        predecessorsAfterMigration.forEach(migrationPredecessor ->
                predecessorsBeforeMigration.add(findMatching(migrationPredecessor, pipelineGraphBeforeMigration)));
    }

    private void findPredecessorsInMigrationPipeline(PipelineGraph pipelineGraphAfterMigration) {
        PipelineGraphHelpers.findStreams(pipelineGraphAfterMigration).forEach(stream ->
                predecessorsAfterMigration.addAll(getPredecessors(stream, migrationEntity.getTargetElement(),
                        pipelineGraphAfterMigration, new ArrayList<>())));
    }

    private List<SpDataSet> findDataSets() {
        return pipeline.getStreams().stream()
                .filter(s -> s instanceof SpDataSet)
                .map(s -> new SpDataSet((SpDataSet) s))
                .collect(Collectors.toList());
    }

    private void buildGraphWithRelays(PipelineGraph pipelineGraphAfterMigration) {
        new InvocationGraphBuilder(pipelineGraphAfterMigration, pipeline.getPipelineId()).buildGraphs();
    }

    private void purgeExistingRelays() {
        pipeline.getSepas().forEach(s -> s.setOutputStreamRelays(new ArrayList<>()));
    }

    private void updateMigrationStatus(PipelineOperationStatus partialStatus, PipelineOperationStatus status) {
        // Add status to global migration status
        partialStatus.getElementStatus().forEach(status::addPipelineElementStatus);
    }

    private List<SpDataStreamRelayContainer> extractRelaysFromDataProcessor(List<InvocableStreamPipesEntity> graphs) {
        return graphs.stream()
                .map(DataProcessorInvocation.class::cast)
                .map(SpDataStreamRelayContainer::new)
                .collect(Collectors.toList());
    }

    private Set<String> extractUniqueRelayIds(PipelineOperationStatus status) {
        return status.getElementStatus().stream()
                .filter(PipelineElementStatus::isSuccess)
                .map(PipelineElementStatus::getElementId)
                .collect(Collectors.toSet());
    }
}
