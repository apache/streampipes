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

import org.apache.streampipes.manager.function.FunctionRegistrationService;
import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.model.function.FunctionDefinition;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v2/functions")
public class FunctionsResource extends AbstractAuthGuardedRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getActiveFunctions() {
    return ok(FunctionRegistrationService.INSTANCE.getAllFunctions());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response registerFunctions(List<FunctionDefinition> functions) {
    functions.forEach(FunctionRegistrationService.INSTANCE::registerFunction);
    return ok(Notifications.success("Function successfully registered"));
  }

  @DELETE
  @Path("{functionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deregisterFunction(@PathParam("functionId") String functionId) {
    FunctionRegistrationService.INSTANCE.deregisterFunction(functionId);
    return ok(Notifications.success("Function successfully deregistered"));
  }

  @GET
  @Path("{functionId}/metrics")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFunctionMetrics(@PathParam("functionId") String functionId) {
    return ok(ExtensionsLogProvider.INSTANCE.getMetricInfosForResource(functionId));
  }

  @GET
  @Path("{functionId}/logs")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFunctionLogs(@PathParam("functionId") String functionId) {
    return ok(ExtensionsLogProvider.INSTANCE.getLogInfosForResource(functionId));
  }
}
