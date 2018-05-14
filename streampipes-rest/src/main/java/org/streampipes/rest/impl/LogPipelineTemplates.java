package org.streampipes.rest.impl;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.pipeline.PipelineOperationStatus;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateDescriptionContainer;
import org.streampipes.model.template.PipelineTemplateInvocation;
import org.streampipes.rest.api.LogPipelineTemplate;
import org.streampipes.sdk.builder.BoundPipelineElementBuilder;
import org.streampipes.sdk.builder.PipelineTemplateBuilder;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.management.StorageDispatcher;
import org.streampipes.vocabulary.StreamPipes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Path("/v2/users/{username}/log-pipeline-templates")
public class LogPipelineTemplates extends AbstractRestInterface implements LogPipelineTemplate {
    //TODO: Interface

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //Returns all log-pipeline Invocations
    public Response getPipelineTemplateInvocation() {
        try {
            List<PipelineTemplateDescription> descriptions = Arrays.asList(makeSaveToElasticTemplate());
            String jsonLd = toJsonLd(new PipelineTemplateDescriptionContainer(descriptions));
            return ok(jsonLd);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response generatePipeline(@PathParam("username") String username, String pipelineTemplateInvocationString) {
        try {
            PipelineTemplateInvocation pipelineTemplateInvocation = new JsonLdTransformer(StreamPipes.PIPELINE_TEMPLATE_INVOCATION).fromJsonLd(pipelineTemplateInvocationString, PipelineTemplateInvocation.class);
            PipelineOperationStatus status = Operations.handlePipelineTemplateInvocation(username, pipelineTemplateInvocation);

            return ok(status);
        } catch (IOException e) {
            e.printStackTrace();
            return fail();
        }
    }


    private PipelineTemplateDescription makeSaveToElasticTemplate() throws URISyntaxException {
        return new PipelineTemplateDescription(PipelineTemplateBuilder.create("logs-to-Elastic", "Save Logs", "Save all logs in Elastic-Search")
                .boundPipelineElementTemplate(BoundPipelineElementBuilder
                    .create(getProcessor("http://localhost:8089/sep/log"))
                .connectTo(BoundPipelineElementBuilder
                    .create(getSink("http://pe-flink-samples:8090/sec/elasticsearch"))
                        .withPredefinedFreeTextValue("index-name", "streampipes-log")
                        .withPredefinedSelection("timestamp", Collections.singletonList("time"))
                    .build())
                .build())
        .build());
    }

    private DataProcessorDescription getProcessor(String id) throws URISyntaxException {
        return getStorage()
                .getSEPAById(id);
    }

    private DataSinkDescription getSink(String id) throws URISyntaxException {
        return getStorage()
                .getSECById(id);
    }

    private IPipelineElementDescriptionStorage getStorage() {
        return StorageDispatcher
                .INSTANCE
                .getTripleStore()
                .getStorageAPI();
    }


}
