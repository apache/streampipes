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

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;
import org.streampipes.rest.api.IPipelineTemplate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/v2/users/{username}/pipeline-templates")
public class PipelineTemplate extends AbstractRestInterface implements IPipelineTemplate {

  @GET
  @Path("/streams")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAvailableDataStreams() {
    return null;
  }

  @GET
  @Path("/sets")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAvailableDataSets() {

    List<DataSourceDescription> sources = getPipelineElementRdfStorage().getAllSEPs();
    List<SpDataSet> datasets = new ArrayList<>();

    for(DataSourceDescription source : sources) {
      source
              .getSpDataStreams()
              .stream()
              .filter(stream -> stream instanceof SpDataSet)
              .forEach(set -> datasets.add((SpDataSet) set));
    }

    return ok(datasets);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAllPipelineTemplates() {
    return ok(Operations.getAllPipelineTemplates());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getPipelineTemplates(@QueryParam("streamId") String streamId) {
    return ok(Operations.getCompatiblePipelineTemplates(streamId));
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response generatePipeline(PipelineTemplateInvocation pipelineTemplateInvocation) {
    // TODO
    return null;
  }
}
