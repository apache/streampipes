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

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.model.labeling.Label;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.management.StorageDispatcher;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/v2/labeling/label")
public class LabelResource extends AbstractRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getAllLabels() {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getAllLabels());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response addLabel(Label label) {
    Category categoryForLabel = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .getCategory(label.getCategoryId());
    if (categoryForLabel == null) {
      String resString = String.format("Category with categoryId %s does not exist", label.getCategoryId());
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    String labelId = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .storeLabel(label);

    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getLabel(labelId));
  }

  @GET
  @Path("/{labelId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getLabel(@PathParam("labelId") String labelId) {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getLabel(labelId));
  }

  @PUT
  @Path("/{labelId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response updateLabel(@PathParam("labelId") String labelId, Label label) {
    if (!labelId.equals(label.getId())) {
      String resString = "LabelId not the same as in message body";
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    Category categoryForLabel = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .getCategory(label.getCategoryId());
    if (categoryForLabel == null) {
      String resString = String.format("Category with categoryId %s does not exist", label.getCategoryId());
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .updateLabel(label);

    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getLabel(labelId));
  }

  @DELETE
  @Path("/{labelId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response deleteLabel(@PathParam("labelId") String labelId) {
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .deleteLabel(labelId);
    return ok();
  }

  @GET
  @Path("category/{categoryId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getLabelsForCategory(@PathParam("categoryId") String categoryId) {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getAllForCategory(categoryId));
  }
}
