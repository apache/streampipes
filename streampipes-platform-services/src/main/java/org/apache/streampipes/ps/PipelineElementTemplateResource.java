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
package org.apache.streampipes.ps;

import org.apache.streampipes.manager.template.DataProcessorTemplateHandler;
import org.apache.streampipes.manager.template.DataSinkTemplateHandler;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.rest.impl.AbstractRestInterface;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/pipeline-element-templates")
public class PipelineElementTemplateResource extends AbstractRestInterface {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getAll(@QueryParam("appId") String appId) {
    if (appId == null) {
      return ok(getPipelineElementTemplateStorage().getAll());
    } else {
      return ok(getPipelineElementTemplateStorage().getPipelineElementTemplatesforAppId(appId));
    }
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getById(@PathParam("id") String s) {
    try {
      return ok(getPipelineElementTemplateStorage().getElementById(s));
    } catch (RuntimeException e) {
      return badRequest();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response create(PipelineElementTemplate entity) {
    getPipelineElementTemplateStorage().createElement(entity);
    return ok();
  }

  @PUT
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response update(@PathParam("id") String id, PipelineElementTemplate entity) {
    try {
      if (id.equals(entity.getCouchDbId())) {
        return ok(getPipelineElementTemplateStorage().updateElement(entity));
      } else {
        return badRequest();
      }
    } catch (RuntimeException e) {
      return badRequest();
    }
  }

  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") String s) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(s);
    getPipelineElementTemplateStorage().deleteElement(template);
    return ok();
  }

  @POST
  @Path("{id}/sink")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getPipelineElementForTemplate(@PathParam("id") String id,
                                                @QueryParam("overwriteNames") String overwriteNameAndDescription,
                                                DataSinkInvocation invocation) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    return ok(new DataSinkTemplateHandler(template, invocation, Boolean.parseBoolean(overwriteNameAndDescription))
            .applyTemplateOnPipelineElement());
  }

  @POST
  @Path("{id}/processor")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getPipelineElementForTemplate(@PathParam("id") String id,
                                                @QueryParam("overwriteNames") String overwriteNameAndDescription,
                                                DataProcessorInvocation invocation) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    return ok(new DataProcessorTemplateHandler(template, invocation, Boolean.parseBoolean(overwriteNameAndDescription))
            .applyTemplateOnPipelineElement());
  }


}
