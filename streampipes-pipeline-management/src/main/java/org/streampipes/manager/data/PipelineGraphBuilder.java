/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.data;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineGraphBuilder {

    private Pipeline pipeline;
    private List<NamedStreamPipesEntity> allPipelineElements;
    private List<InvocableStreamPipesEntity> invocableElements;

    public PipelineGraphBuilder(Pipeline pipeline) {
        this.pipeline = pipeline;
        this.allPipelineElements = addAll();
        this.invocableElements = addInvocable();
    }

    private List<NamedStreamPipesEntity> addAll() {
        List<NamedStreamPipesEntity> allElements = new ArrayList<>();
        allElements.addAll(pipeline.getStreams());
        allElements.addAll(addInvocable());
        return allElements;
    }

    private List<InvocableStreamPipesEntity> addInvocable() {
        List<InvocableStreamPipesEntity> allElements = new ArrayList<>();
        allElements.addAll(pipeline.getSepas());
        allElements.addAll(pipeline.getActions());
        return allElements;
    }


    public PipelineGraph buildGraph() {
        PipelineGraph pipelineGraph = new PipelineGraph();
        allPipelineElements.forEach(p -> pipelineGraph.addVertex(p));

        for(NamedStreamPipesEntity source : allPipelineElements) {
            List<InvocableStreamPipesEntity> targets = findTargets(source.getDOM());
            targets.forEach(t -> pipelineGraph.addEdge(source, t));
        }

        return pipelineGraph;
    }

    private List<InvocableStreamPipesEntity> findTargets(String domId) {
        return invocableElements
                .stream()
                .filter(i -> i.getConnectedTo().contains(domId))
                .collect(Collectors.toList());
    }

}
