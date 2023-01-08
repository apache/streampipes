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

import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.units.UnitProvider;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/units")
public class OntologyMeasurementUnit extends AbstractRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/instances")
  public Response getAllUnits() {
    return ok(UnitProvider
        .INSTANCE
        .getAvailableUnits());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/types")
  public Response getAllUnitTypes() {
    return ok(UnitProvider
        .INSTANCE
        .getAvailableUnitTypes());
  }

  @GET
  @Path("/instances/{resourceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUnit(@PathParam("resourceId") String resourceUri) {
    return ok(UnitProvider
        .INSTANCE
        .getUnit(resourceUri));
  }

}
