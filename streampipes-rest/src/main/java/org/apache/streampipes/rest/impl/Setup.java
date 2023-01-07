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


import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import com.google.gson.JsonObject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/setup")
public class Setup extends AbstractRestResource {

  @GET
  @Path("/configured")
  @Produces(MediaType.APPLICATION_JSON)
  public Response isConfigured() {
    JsonObject obj = new JsonObject();
    if (BackendConfig.INSTANCE.isConfigured()) {
      obj.addProperty("configured", true);
      return ok(obj.toString());
    } else {
      obj.addProperty("configured", false);
      obj.addProperty("setupRunning", BackendConfig.INSTANCE.isConfigured());
      return ok(obj.toString());
    }
  }
}
