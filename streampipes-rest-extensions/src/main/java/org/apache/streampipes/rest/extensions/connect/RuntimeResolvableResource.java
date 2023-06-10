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

package org.apache.streampipes.rest.extensions.connect;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.api.RuntimeResolvableRequestHandler;
import org.apache.streampipes.extensions.management.connect.RuntimeResovable;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/worker/resolvable")
public class RuntimeResolvableResource extends AbstractSharedRestInterface {

  @POST
  @Path("{id}/configurations")
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response fetchConfigurations(@PathParam("id") String elementId,
                                      RuntimeOptionsRequest runtimeOptionsRequest) {

    var adapter = RuntimeResovable.getAdapter(elementId);
    RuntimeOptionsResponse response;
    RuntimeResolvableRequestHandler handler = new RuntimeResolvableRequestHandler();

    try {
      if (adapter instanceof ResolvesContainerProvidedOptions) {
        response = handler.handleRuntimeResponse((ResolvesContainerProvidedOptions) adapter, runtimeOptionsRequest);
        return ok(response);
      } else if (adapter instanceof SupportsRuntimeConfig) {
        response = handler.handleRuntimeResponse((SupportsRuntimeConfig) adapter, runtimeOptionsRequest);
        return ok(response);
      } else {
        throw new SpRuntimeException(
            "This element does not support dynamic options - is the pipeline element description up to date?");
      }
    } catch (SpConfigurationException e) {
      return jakarta.ws.rs.core.Response
          .status(400)
          .entity(e)
          .build();
    }
  }
}
