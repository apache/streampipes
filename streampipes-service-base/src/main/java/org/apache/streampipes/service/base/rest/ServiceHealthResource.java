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

package org.apache.streampipes.service.base.rest;

import org.apache.streampipes.service.base.StreamPipesServiceBase;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("")
public class ServiceHealthResource {

  @GET()
  @Path("svchealth/{serviceId}")
  public Response healthy(@PathParam("serviceId") String serviceId) {
    if (serviceId.equals(StreamPipesServiceBase.AUTO_GENERATED_SERVICE_ID)) {
      return Response.ok().build();
    } else {
      return Response.status(404).build();
    }
  }
}
