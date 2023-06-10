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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/pipeline-element/runtime")
public class PipelineElementRuntimeInfo extends AbstractRestResource {

  @POST
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getRuntimeInfo(SpDataStream spDataStream) {
    try {
      return ok(Operations.getRuntimeInfo(spDataStream));
    } catch (SpRuntimeException e) {
      return statusMessage(Notifications.error("Could not get runtime data"));
    }
  }
}
