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

import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;

import java.util.List;
import java.util.stream.Collectors;

public class PipelineGraphHelpers {

    public static List<SpDataStream> findStreams(PipelineGraph pipelineGraph) {
        return find(pipelineGraph, SpDataStream.class);
    }

    public static List<InvocableStreamPipesEntity> findInvocableElements(PipelineGraph pipelineGraph) {
        return find(pipelineGraph, InvocableStreamPipesEntity.class);
    }

    private static <T> List<T> find(PipelineGraph pipelineGraph, Class<T> clazz) {
        return pipelineGraph
                .vertexSet()
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
}
