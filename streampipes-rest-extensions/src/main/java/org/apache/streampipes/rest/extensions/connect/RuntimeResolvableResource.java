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
import org.apache.streampipes.extensions.api.connect.Connector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.api.RuntimeResolvableRequestHandler;
import org.apache.streampipes.extensions.management.connect.RuntimeResovable;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/worker/resolvable")
public class RuntimeResolvableResource extends AbstractSharedRestInterface {

  @POST
  @Path("{id}/configurations")
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response fetchConfigurations(@PathParam("id") String elementId,
                                      RuntimeOptionsRequest runtimeOptionsRequest) {

    Connector connector = RuntimeResovable.getAdapterOrProtocol(elementId);
    RuntimeOptionsResponse response;
    RuntimeResolvableRequestHandler handler = new RuntimeResolvableRequestHandler();

    try {
      if (connector instanceof ResolvesContainerProvidedOptions) {
        response = handler.handleRuntimeResponse((ResolvesContainerProvidedOptions) connector, runtimeOptionsRequest);
        return ok(response);
      } else if (connector instanceof SupportsRuntimeConfig) {
        response = handler.handleRuntimeResponse((SupportsRuntimeConfig) connector, runtimeOptionsRequest);
        return ok(response);
      } else {
        throw new SpRuntimeException(
            "This element does not support dynamic options - is the pipeline element description up to date?");
      }
    } catch (SpConfigurationException e) {
      return javax.ws.rs.core.Response
          .status(400)
          .entity(e)
          .build();
    }
  }
}
