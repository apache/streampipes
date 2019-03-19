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

package org.streampipes.container.api;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.container.init.RunningInstances;
import org.streampipes.container.transform.Transformer;
import org.streampipes.container.util.Util;
import org.streampipes.model.Response;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.runtime.RuntimeOptions;
import org.streampipes.model.runtime.RuntimeOptionsRequest;
import org.streampipes.model.runtime.RuntimeOptionsResponse;
import org.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.streampipes.serializers.json.GsonSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public abstract class InvocableElement<I extends InvocableStreamPipesEntity, D extends Declarer,
        P extends AbstractParameterExtractor<I>> extends Element<D> {

    protected abstract Map<String, D> getElementDeclarers();
    protected abstract String getInstanceId(String uri, String elementId);

    protected Class<I> clazz;

    public InvocableElement(Class<I> clazz) {
        this.clazz = clazz;
    }

    @POST
    @Path("{elementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invokeRuntime(@PathParam("elementId") String elementId, String payload) {

        try {
            I graph = Transformer.fromJsonLd(clazz, payload);
            InvocableDeclarer declarer = (InvocableDeclarer) getDeclarerById(elementId);

            if (declarer != null) {
                String runningInstanceId = getInstanceId(graph.getElementId(), elementId);
                RunningInstances.INSTANCE.add(runningInstanceId, graph, declarer.getClass().newInstance());
                Response resp = RunningInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(graph);
                return Util.toResponseString(resp);
            }
        } catch (RDFParseException | IOException | RepositoryException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return Util.toResponseString(new Response(elementId, false, e.getMessage()));
        }

        return Util.toResponseString(elementId, false, "Could not find the element with id: " + elementId);
    }

    @POST
    @Path("{elementId}/configurations")
    //@Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public String fetchConfigurations(@PathParam("elementId") String elementId, String payload) {

        RuntimeOptionsRequest runtimeOptionsRequest = GsonSerializer.getGsonWithIds().fromJson(payload,
                RuntimeOptionsRequest.class);
        ResolvesContainerProvidedOptions resolvesOptions = (ResolvesContainerProvidedOptions) getDeclarerById(elementId);

        List<RuntimeOptions> availableOptions = resolvesOptions.resolveOptions(runtimeOptionsRequest.getRequestId(),
                runtimeOptionsRequest.getMappedEventProperty());

        return GsonSerializer.getGsonWithIds().toJson(new RuntimeOptionsResponse(runtimeOptionsRequest,
                availableOptions));

    }

    @POST
    @Path("{elementId}/output")
    //@Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public String fetchOutputStrategy(@PathParam("elementId") String elementId, String payload) {

        I runtimeOptionsRequest = GsonSerializer.getGsonWithIds().fromJson(payload, clazz);
        ResolvesContainerProvidedOutputStrategy<I, P> resolvesOutput =
                (ResolvesContainerProvidedOutputStrategy<I, P>)
                getDeclarerById
                (elementId);

        try {
            return GsonSerializer.getGsonWithIds().toJson(resolvesOutput.resolveOutputStrategy
                    (runtimeOptionsRequest, getExtractor(runtimeOptionsRequest)));
        } catch (SpRuntimeException e) {
            e.printStackTrace();
            return Util.toResponseString(runtimeOptionsRequest.getElementId(), false);
        }
    }


    // TODO move endpoint to /elementId/instances/runningInstanceId
    @DELETE
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detach(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {

        InvocableDeclarer runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);

        if (runningInstance != null) {
            Response resp = runningInstance.detachRuntime(runningInstanceId);

            if (resp.isSuccess()) {
                RunningInstances.INSTANCE.remove(runningInstanceId);
            }

            return Util.toResponseString(resp);
        }

        return Util.toResponseString(elementId, false, "Could not find the running instance with id: " + runningInstanceId);
    }

    protected abstract P getExtractor(I graph);
}

