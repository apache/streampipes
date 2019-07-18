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

package org.streampipes.rest.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.pipeline.PipelineOperationStatus;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;
import org.streampipes.rest.api.InternalPipelineTemplate;
import org.streampipes.sdk.builder.BoundPipelineElementBuilder;
import org.streampipes.sdk.builder.PipelineTemplateBuilder;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.management.StorageDispatcher;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/internal-pipelines")
public class InternalPipelineTemplates extends AbstractRestInterface implements InternalPipelineTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(InternalPipelineTemplates.class);
    private Map<String, Template> templates;

    public InternalPipelineTemplates() {
        templates = new HashMap<>();
        templates.put("Save Logs", new Template() {
            @Override
            public PipelineTemplateDescription makeTemplate() throws URISyntaxException {
                return new PipelineTemplateDescription(PipelineTemplateBuilder.create("logs-to-Elastic", "Save Logs", "Save all logs in Elastic-Search")
                        .boundPipelineElementTemplate(BoundPipelineElementBuilder
                                .create(getSink("org.streampipes.pe.flink.elasticsearch"))
                                .withPredefinedFreeTextValue("index-name", "streampipes-log")
                                .withPredefinedSelection("timestamp", Collections.singletonList("epochTime"))
                                .build())
                        .build());
            }
        });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //Returns all log-preprocessing Invocations
    public Response getPipelineTemplateInvocation() {
        Object[] templateNames = templates.keySet().toArray();
        String templateJSON = toJson(templateNames);
        return ok(templateJSON);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response generatePipeline(@PathParam("username") String username, String pipelineId) {
        try {
            PipelineTemplateDescription pipelineTemplateDescription = templates.get(pipelineId).makeTemplate();

            PipelineTemplateInvocation invocation = Operations.getPipelineInvocationTemplate(getLogDataStream(), pipelineTemplateDescription);
            PipelineOperationStatus status = Operations.handlePipelineTemplateInvocation(username, invocation, pipelineTemplateDescription);

            return ok(status);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return fail();
        }
    }

    private DataProcessorDescription getProcessor(String id) throws URISyntaxException {
        return getStorage()
                .getSEPAById(id);
    }

    private DataSinkDescription getSink(String id) throws URISyntaxException {
        return getStorage()
                .getSECByAppId(id);
    }

    private IPipelineElementDescriptionStorage getStorage() {
        return StorageDispatcher
                .INSTANCE
                .getTripleStore()
                .getStorageAPI();
    }

    private List<SpDataStream> getAllDataStreams() {
        List<DataSourceDescription> sources = getPipelineElementRdfStorage().getAllSEPs();
        List<SpDataStream> datasets = new ArrayList<>();
        for (DataSourceDescription source : sources) {
            datasets.addAll(source
                    .getSpDataStreams());
        }

        return datasets;
    }

    private SpDataStream getLogDataStream() {
        return new SpDataStream(getAllDataStreams()
                .stream()
                .filter(sp -> sp.getAppId() != null)
                .filter(sp -> sp.getAppId().equals("org.streampipes.sources.log.stream"))
                .findFirst()
                .get());
    }

    private interface Template {
        PipelineTemplateDescription makeTemplate() throws URISyntaxException;
    }


}
