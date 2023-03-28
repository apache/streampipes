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

import org.apache.streampipes.manager.info.SystemInfoProvider;
import org.apache.streampipes.manager.info.VersionInfoProvider;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/info")
public class Version extends AbstractAuthGuardedRestResource {

  @GET
  @Path("/versions")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Provides health-check and information about current backend version.", tags = {"Version"})
  public Response getVersionInfo() {
    return ok(new VersionInfoProvider().makeVersionInfo());
  }

  @GET
  @Path("/system")
  public Response getSystemInfo() {
    return ok(new SystemInfoProvider().getSystemInfo());
  }


}
