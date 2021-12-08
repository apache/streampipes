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
package org.apache.streampipes.manager.execution.pipeline.executor.utils;

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.matching.InvocationGraphBuilder;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PipelineUtils {

    public static List<SpDataSet> findDataSets(Pipeline pipeline) {
        return pipeline.getStreams().stream()
                .filter(s -> s instanceof SpDataSet)
                .map(s -> new SpDataSet((SpDataSet) s))
                .collect(Collectors.toList());
    }

    public static List<NamedStreamPipesEntity> getPredecessors(NamedStreamPipesEntity source,
                                                           InvocableStreamPipesEntity target,
                                                           PipelineGraph pipelineGraph,
                                                           List<NamedStreamPipesEntity> foundPredecessors){

        Set<InvocableStreamPipesEntity> targets = getTargetsAsSet(source, pipelineGraph,
                InvocableStreamPipesEntity.class);

        //TODO: Check if this works for all graph topologies
        if (targets.contains(target)){
            foundPredecessors.add(source);
        } else {
            List<NamedStreamPipesEntity> successors = getTargetsAsList(source, pipelineGraph,
                    NamedStreamPipesEntity.class);

            if (successors.isEmpty()) return foundPredecessors;
            successors.forEach(successor -> getPredecessors(successor, target, pipelineGraph, foundPredecessors));
        }
        return foundPredecessors;
    }

    private static <T> List<T> getTargetsAsList(NamedStreamPipesEntity source, PipelineGraph pipelineGraph,
                                         Class<T> clazz){
        return new ArrayList<>(getTargetsAsSet(source, pipelineGraph, clazz));
    }

    private static <T> Set<T> getTargetsAsSet(NamedStreamPipesEntity source, PipelineGraph pipelineGraph,
                                       Class<T> clazz){
        return pipelineGraph.outgoingEdgesOf(source)
                .stream()
                .map(pipelineGraph::getEdgeTarget)
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    public static NamedStreamPipesEntity findMatching(NamedStreamPipesEntity entity, PipelineGraph pipelineGraph){
        AtomicReference<NamedStreamPipesEntity> match = new AtomicReference<>();
        List<SpDataStream> dataStreams = PipelineGraphHelpers.findStreams(pipelineGraph);

        for (SpDataStream stream : dataStreams) {
            NamedStreamPipesEntity foundEntity = compareGraphs(stream, entity, pipelineGraph, new ArrayList<>());
            if (foundEntity != null) {
                match.set(foundEntity);
            }
        }
        return match.get();
    }

    private static NamedStreamPipesEntity compareGraphs(NamedStreamPipesEntity source,
                                                 NamedStreamPipesEntity searchedEntity,
                                                 PipelineGraph pipelineGraph,
                                                 List<NamedStreamPipesEntity> successors){
        if(matchingDOM(source, searchedEntity)) {
            return source;
        } else if (successors.isEmpty()) {
            successors = getTargetsAsList(source, pipelineGraph, NamedStreamPipesEntity.class);
            Optional<NamedStreamPipesEntity> successor = successors.stream().findFirst();
            if (successor.isPresent()) {
                successors.remove(successor.get());
                return compareGraphs(successor.get(), searchedEntity, pipelineGraph, successors);
            }
        }
        return null;
    }

    /**
     * Checks if DOM are equal
     *
     * @param source pipeline element
     * @param target pipeline element
     * @return true if DOM is the same, else false
     */
    private static boolean matchingDOM(NamedStreamPipesEntity source, NamedStreamPipesEntity target) {
        return source.getDOM().equals(target.getDOM());
    }

    public static void purgeExistingRelays(Pipeline pipeline) {
        pipeline.getSepas().forEach(s -> s.setOutputStreamRelays(new ArrayList<>()));
    }

    public static void buildPipelineGraph(PipelineGraph pipelineGraphAfterMigration, String pipelineId) {
        new InvocationGraphBuilder(pipelineGraphAfterMigration,
                pipelineId).buildGraphs();
    }
}
