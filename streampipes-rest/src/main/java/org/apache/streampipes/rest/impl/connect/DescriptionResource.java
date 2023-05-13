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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.DescriptionManagement;
import org.apache.streampipes.connect.management.management.WorkerUrlProvider;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/v2/connect/master/description")
public class DescriptionResource extends AbstractAdapterResource<DescriptionManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(DescriptionResource.class);
  private WorkerUrlProvider workerUrlProvider;

  public DescriptionResource() {
    super(DescriptionManagement::new);
    workerUrlProvider = new WorkerUrlProvider();
  }

  @GET
  @JacksonSerialized
  @Path("/adapters")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAdapters() {
    List<AdapterDescription> result = managementService.getAdapters();

    return ok(result);
  }

  @GET
  @Path("/{id}/assets")
  @Produces("application/zip")
  public Response getAdapterAssets(@PathParam("id") String id) {
    try {
      String result = null;

      Optional<AdapterDescription> adapterDescriptionOptional = managementService.getAdapter(id);
      if (adapterDescriptionOptional.isPresent()) {
        AdapterDescription adapterDescription = adapterDescriptionOptional.get();
        String workerUrl = workerUrlProvider.getWorkerUrl(adapterDescription.getAppId());

        result = managementService.getAssets(workerUrl);
      }

      if (result == null) {
        LOG.error("Not found adapter with id " + id);
        return fail();
      } else {
        return ok(result);
      }
    } catch (AdapterException e) {
      LOG.error("Not found adapter with id " + id, e);
      return fail();
    } catch (NoServiceEndpointsAvailableException e) {
      return fail();
    }
  }

  @GET
  @Path("/{id}/assets/icon")
  @Produces("image/png")
  public Response getAdapterIconAsset(@PathParam("id") String id) {
    try {

      byte[] result = null;

      Optional<AdapterDescription> adapterDescriptionOptional = managementService.getAdapter(id);
      if (adapterDescriptionOptional.isPresent()) {
        AdapterDescription adapterDescription = adapterDescriptionOptional.get();
        String workerUrl = workerUrlProvider.getWorkerUrl(adapterDescription.getAppId());

        result = managementService.getIconAsset(workerUrl);
      }

      if (result == null) {
        LOG.error("Not found adapter with id " + id);
        return fail();
      } else {
        return ok(result);
      }
    } catch (AdapterException e) {
      LOG.error("Not found adapter with id " + id);
      return fail();
    } catch (NoServiceEndpointsAvailableException e) {
      return fail();
    }
  }

  @GET
  @Path("/{id}/assets/documentation")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getAdapterDocumentationAsset(@PathParam("id") String id) {
    try {
      String result = null;

      Optional<AdapterDescription> adapterDescriptionOptional = managementService.getAdapter(id);
      if (adapterDescriptionOptional.isPresent()) {
        AdapterDescription adapterDescription = adapterDescriptionOptional.get();
        String workerUrl = workerUrlProvider.getWorkerUrl(adapterDescription.getAppId());

        result = managementService.getDocumentationAsset(workerUrl);
      }

      if (result == null) {
        LOG.error("Not found adapter with id " + id);
        return fail();
      } else {
        return ok(result);
      }
    } catch (AdapterException e) {
      LOG.error("Not found adapter with id " + id, e);
      return fail();
    } catch (NoServiceEndpointsAvailableException e) {
      return fail();
    }
  }

  @DELETE
  @Path("{adapterId}")
  public Response deleteAdapter(@PathParam("adapterId") String adapterId) {
    try {
      this.managementService.deleteAdapterDescription(adapterId);
      return ok();
    } catch (SpRuntimeException e) {
      return badRequest(e);
    }
  }
}
