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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/v2/pipeline-templates")
public class PipelineTemplate extends AbstractAuthGuardedRestResource {

  @GET
  @Path("/streams")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAvailableDataStreams() {
    List<SpDataStream> sources = getPipelineElementRdfStorage().getAllDataStreams();
    List<SpDataStream> datasets = new ArrayList<>();

    sources.stream()
        .filter(stream -> !(stream instanceof SpDataSet))
        .map(SpDataStream::new)
        .forEach(datasets::add);

    return ok((new SpDataStreamContainer(datasets)));
  }

  @GET
  @Path("/sets")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAvailableDataSets() {

    List<SpDataStream> sources = getPipelineElementRdfStorage().getAllDataStreams();
    List<SpDataStream> datasets = new ArrayList<>();

    sources
        .stream()
        .filter(stream -> stream instanceof SpDataSet)
        .map(stream -> new SpDataSet((SpDataSet) stream))
        .forEach(datasets::add);

    return ok(new SpDataStreamContainer(datasets));
  }

  @GET
  @Path("/invocation")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPipelineTemplateInvocation(@QueryParam("streamId") String streamId,
                                                @QueryParam("templateId") String pipelineTemplateId) {
    if (pipelineTemplateId != null) {
      SpDataStream dataStream = getDataStream(streamId);
      PipelineTemplateDescription pipelineTemplateDescription = getPipelineTemplateDescription(pipelineTemplateId);
      PipelineTemplateInvocation invocation =
          Operations.getPipelineInvocationTemplate(dataStream, pipelineTemplateDescription);
      PipelineTemplateInvocation clonedInvocation = new PipelineTemplateInvocation(invocation);
      return ok(new PipelineTemplateInvocation(clonedInvocation));
    } else {
      return fail();
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response generatePipeline(PipelineTemplateInvocation pipelineTemplateInvocation) {

    PipelineOperationStatus status = Operations
        .handlePipelineTemplateInvocation(getAuthenticatedUserSid(), pipelineTemplateInvocation);

    return ok(status);

  }


  private PipelineTemplateDescription getPipelineTemplateDescription(String pipelineTemplateId) {
    return Operations
        .getAllPipelineTemplates()
        .stream()
        .filter(pt -> pt.getAppId().equals(pipelineTemplateId))
        .findFirst()
        .get();
  }

  private List<SpDataStream> getAllDataStreams() {
    return getPipelineElementRdfStorage().getAllDataStreams();
  }

  private SpDataStream getDataStream(String streamId) {
    return getAllDataStreams()
        .stream()
        .filter(sp -> sp.getElementId().equals(streamId))
        .findFirst()
        .get();
  }
}
