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

package org.streampipes.wrapper.spark;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorInvocation;

/**
 * Created by Jochen Lutz on 2017-11-28.
 */
public abstract class AbstractSparkDeclarer<D extends NamedStreamPipesEntity, I extends InvocableStreamPipesEntity, SR extends SparkRuntime> implements InvocableDeclarer<D, I> {
    protected SR runtime;
    protected I graph;

    @Override
    public Response invokeRuntime(I sepaInvocation) {
        runtime = getRuntime(sepaInvocation);
        graph = sepaInvocation;

        if (runtime.startExecution()) {
            return new Response(graph.getElementId(), true);
        }
        else {
            return new Response(graph.getElementId(), false);
        }
    }

    @Override
    public Response detachRuntime(String s) {
        if (runtime.stop()) {
            return new Response(graph.getElementId(), true);
        }
        else {
            return new Response(graph.getElementId(), false);
        }
    }

    protected abstract SR getRuntime(I graph);
}
