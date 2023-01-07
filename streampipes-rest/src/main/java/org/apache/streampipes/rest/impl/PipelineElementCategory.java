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

import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.Category;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.management.StorageManager;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/categories")
public class PipelineElementCategory extends AbstractRestResource {

  @GET
  @Path("/ep")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getEps() {
    return ok(makeCategories(StorageManager.INSTANCE.getPipelineElementStorage().getAllDataStreams()));
  }

  @GET
  @Path("/epa")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getEpaCategories() {
    return ok(DataProcessorType.values());
  }

  @GET
  @Path("/adapter")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getAdapterCategories() {
    return ok(AdapterType.values());
  }

  @GET
  @Path("/ec")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getEcCategories() {
    return ok(DataSinkType.values());
  }

  private List<Category> makeCategories(List<SpDataStream> streams) {
    return streams
        .stream()
        .map(p -> new Category(p.getElementId(), p.getName(), p.getDescription()))
        .collect(Collectors.toList());
  }
}
