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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.rest.api.IOntologyPipelineElement;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.storage.management.StorageManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/v2/ontology")
public class OntologyPipelineElement extends AbstractRestInterface implements IOntologyPipelineElement {

  @Override
  @Path("/sources")
  @GET
  @GsonWithIds
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStreams() {
    List<SpDataStream> result = new ArrayList<>();
    List<SpDataStream> sesameSeps = StorageManager.INSTANCE.getPipelineElementStorage().getAllDataStreams();

    for (SpDataStream sep : sesameSeps) {
      result.add(new SpDataStream(sep));
    }
    return ok(result);
  }

  @Path("/sources/{sourceId}")
  @GET
  @GsonWithIds
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSourceDetails(@PathParam("sourceId") String sepaId, @QueryParam("keepIds") boolean keepIds) {

    SpDataStream sepaDescription = new SpDataStream(StorageManager.INSTANCE.getPipelineElementStorage().getDataStreamById(sepaId));
    return ok(sepaDescription);
  }

  @Override
  @Path("/sepas")
  @GsonWithIds
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSepas() {
    JsonArray result = new JsonArray();
    List<DataProcessorDescription> sesameSepas = StorageManager.INSTANCE.getPipelineElementStorage().getAllDataProcessors();

    for (DataProcessorDescription sepa : sesameSepas) {
      result.add(getHeader(sepa.getUri(), sepa.getName()));
    }
    return ok(result);

  }

  @Override
  @Path("/actions")
  @GET
  @GsonWithIds
  @Produces(MediaType.APPLICATION_JSON)
  public Response getActions() {
    List<DataSinkDescription> result = new ArrayList<>();
    List<DataSinkDescription> sesameSecs = StorageManager.INSTANCE.getPipelineElementStorage().getAllDataSinks();

    for (DataSinkDescription sec : sesameSecs) {
      result.add(new DataSinkDescription(sec));
    }
    return ok(result);
  }

  @Override
  public Response getStream(String streamId, @QueryParam("keepIds") boolean keepIds) {
    // TODO Auto-generated method stub
    return null;
  }

  @Path("/sepas/{sepaId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getSepa(@PathParam("sepaId") String sepaId, @QueryParam("keepIds") boolean keepIds) {

    DataProcessorDescription dataProcessorDescription = new DataProcessorDescription(StorageManager.INSTANCE.getPipelineElementStorage().getDataProcessorById(sepaId));
    return ok(dataProcessorDescription);
  }

  @Path("/actions/{actionId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAction(@PathParam("actionId") String actionId, @QueryParam("keepIds") boolean keepIds) {
    DataSinkDescription dataSinkDescription = new DataSinkDescription(StorageManager.INSTANCE.getPipelineElementStorage().getDataSinkById(actionId));
    return ok(dataSinkDescription);
  }

  private JsonObject getHeader(String uri, String name) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("uri", new JsonPrimitive(uri));
    jsonObj.add("name", new JsonPrimitive(name));

    return jsonObj;
  }
}
