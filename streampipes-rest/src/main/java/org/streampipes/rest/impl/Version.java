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

import org.streampipes.manager.info.SystemInfoProvider;
import org.streampipes.manager.info.VersionInfoProvider;
import org.streampipes.rest.api.IVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/info")
public class Version extends AbstractRestInterface implements IVersion {

  @GET
  @Path("/versions")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getVersionInfo() {
    return ok(new VersionInfoProvider().makeVersionInfo());
  }

  @GET
  @Path("/system")
  @Override
  public Response getSystemInfo() {
    return ok(new SystemInfoProvider().getSystemInfo());
  }


}
