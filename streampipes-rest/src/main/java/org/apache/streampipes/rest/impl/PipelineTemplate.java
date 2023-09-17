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
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.message.Notifications;
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
import java.util.Optional;

@Path("/v2/pipeline-templates")
public class PipelineTemplate extends AbstractAuthGuardedRestResource {

  @GET
  @Path("/streams")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAvailableDataStreams() {
    List<SpDataStream> sources = getPipelineElementRdfStorage().getAllDataStreams();
    List<SpDataStream> datasets = new ArrayList<>();

    sources.stream()
        .map(SpDataStream::new)
        .forEach(datasets::add);

    return ok((new SpDataStreamContainer(datasets)));
  }

  @GET
  @Path("/invocation")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPipelineTemplateInvocation(@QueryParam("streamId") String streamId,
                                                @QueryParam("templateId") String pipelineTemplateId) {
    if (pipelineTemplateId != null) {
      SpDataStream dataStream = getDataStream(streamId);
      var pipelineTemplateDescriptionOpt = getPipelineTemplateDescription(pipelineTemplateId);
      if (pipelineTemplateDescriptionOpt.isPresent()) {
        PipelineTemplateInvocation invocation =
            Operations.getPipelineInvocationTemplate(dataStream, pipelineTemplateDescriptionOpt.get());
        PipelineTemplateInvocation clonedInvocation = new PipelineTemplateInvocation(invocation);
        return ok(new PipelineTemplateInvocation(clonedInvocation));
      } else {
        return badRequest(Notifications.error(
            String.format(
                "Could not create pipeline template %s - did you install all pipeline elements?",
                pipelineTemplateId.substring(pipelineTemplateId.lastIndexOf(".") + 1))
        ));
      }
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


  private Optional<PipelineTemplateDescription> getPipelineTemplateDescription(String pipelineTemplateId) {
    return Operations
        .getAllPipelineTemplates()
        .stream()
        .filter(pt -> pt.getAppId().equals(pipelineTemplateId))
        .findFirst();
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
