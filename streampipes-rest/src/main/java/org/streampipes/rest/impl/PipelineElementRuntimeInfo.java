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

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.api.IPipelineElementRuntimeInfo;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/pipeline-element/runtime")
public class PipelineElementRuntimeInfo extends AbstractRestInterface implements IPipelineElementRuntimeInfo {


  @Override
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response getRuntimeInfo(SpDataStream spDataStream) {
    // TODO currently only supported for data streams. For data sets, a dummy pipeline needs to be generated to get runtime values.
    try {
      return ok(Operations.getRuntimeInfo(spDataStream));
    } catch (SpRuntimeException e) {
      return statusMessage(Notifications.error("Could not get runtime data"));
    }
  }
}
