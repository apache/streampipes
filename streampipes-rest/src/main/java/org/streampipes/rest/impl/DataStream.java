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
import org.streampipes.model.SpDataStream;
import org.streampipes.rest.shared.serializer.annotation.GsonWithIds;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/streams")
public class DataStream extends AbstractRestInterface {

  @Path("/update")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  public Response getStreamsBySource(SpDataStream stream) {
    return ok(Operations.updateActualTopic(stream));
  }
}
