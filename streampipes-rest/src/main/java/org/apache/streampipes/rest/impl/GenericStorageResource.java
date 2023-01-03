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

import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Path("/v2/storage-generic")
@Component
public class GenericStorageResource extends AbstractAuthGuardedRestResource {

  public static final String APP_DOC_NAME = "appDocName";

  private static final Logger LOG = LoggerFactory.getLogger(GenericStorageResource.class);

  @GET
  @Path("{appDocName}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_READ_GENERIC_STORAGE_PRIVILEGE)
  public Response getAll(@PathParam(APP_DOC_NAME) String appDocName) {
    try {
      List<Map<String, Object>> assets = getGenericStorage().findAll(appDocName);
      return ok(assets);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @POST
  @Path("{appDocName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_GENERIC_STORAGE_PRIVILEGE)
  public Response create(@PathParam(APP_DOC_NAME) String appDocName,
                         String document) {
    try {
      Map<String, Object> obj = getGenericStorage().create(document);
      return ok(obj);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @GET
  @Path("{appDocName}/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_READ_GENERIC_STORAGE_PRIVILEGE)
  public Response getCategory(@PathParam(APP_DOC_NAME) String appDocName,
                              @PathParam("id") String documentId) {
    try {
      Map<String, Object> obj = getGenericStorage().findOne(documentId);
      return ok(obj);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @PUT
  @Path("{appDocName}/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_GENERIC_STORAGE_PRIVILEGE)
  public Response update(@PathParam(APP_DOC_NAME) String appDocName,
                         @PathParam("id") String documentId,
                         String document) {
    try {
      Map<String, Object> obj = getGenericStorage().update(documentId, document);
      return ok(obj);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @DELETE
  @Path("{appDocName}/{id}/{rev}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_GENERIC_STORAGE_PRIVILEGE)
  public Response delete(@PathParam(APP_DOC_NAME) String appDocName,
                         @PathParam("id") String documentId,
                         @PathParam("rev") String rev) {
    try {
      getGenericStorage().delete(documentId, rev);
      return ok();
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  private IGenericStorage getGenericStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();
  }

}
