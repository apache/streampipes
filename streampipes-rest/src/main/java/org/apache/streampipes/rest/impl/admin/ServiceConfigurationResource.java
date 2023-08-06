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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.CRUDStorage;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/extensions-services-configurations")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ServiceConfigurationResource extends AbstractAuthGuardedRestResource {

  private final CRUDStorage<String, SpServiceConfiguration> extensionsServicesConfigStorage =
      getNoSqlStorage().getExtensionsServiceConfigurationStorage();


  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllServiceConfigurations() {
    return ok(extensionsServicesConfigStorage.getAll());
  }

  @GET
  @Path("{serviceGroup}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getServiceConfiguration(@PathParam("serviceGroup") String serviceGroup) {
    var config = extensionsServicesConfigStorage.getElementById(serviceGroup);
    return config != null ? ok(config) : notFound();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerServiceConfiguration(SpServiceConfiguration serviceConfiguration) {
    if (extensionsServicesConfigStorage.getElementById(serviceConfiguration.getServiceGroup()) == null) {
      extensionsServicesConfigStorage.createElement(serviceConfiguration);
      return created();
    } else {
      return ok();
    }
  }

  @PUT
  @Path("{serviceGroup}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerServiceConfiguration(@PathParam("serviceGroup") String serviceGroup,
                                               SpServiceConfiguration serviceConfiguration) {
    if (extensionsServicesConfigStorage.getElementById(serviceGroup) != null) {
      extensionsServicesConfigStorage.updateElement(serviceConfiguration);
      return ok();
    } else {
      return badRequest();
    }
  }
}
