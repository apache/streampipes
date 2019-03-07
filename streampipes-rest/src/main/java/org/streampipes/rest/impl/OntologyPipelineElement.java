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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.rest.api.IOntologyPipelineElement;
import org.streampipes.rest.shared.annotation.GsonWithIds;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.storage.management.StorageManager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/ontology")
public class OntologyPipelineElement extends AbstractRestInterface implements IOntologyPipelineElement {

  @Override
  @Path("/sources")
  @GET
  @GsonWithIds
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStreams() {
    List<DataSourceDescription> result = new ArrayList<>();
    List<DataSourceDescription> sesameSeps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();

    for (DataSourceDescription sep : sesameSeps) {
      result.add(new DataSourceDescription(sep));
    }
    return ok(result);
  }

  @Path("/sources/{sourceId}")
  @GET
  @GsonWithIds
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSourceDetails(@PathParam("sourceId") String sepaId, @QueryParam("keepIds") boolean keepIds) {

    try {
      DataSourceDescription sepaDescription = new DataSourceDescription(StorageManager.INSTANCE.getStorageAPI().getSEPById(sepaId));
      return ok(sepaDescription);
    } catch (URISyntaxException e) {
      return ok(Notifications.error("Error"));
    }
  }

  @Override
  @Path("/sepas")
  @GsonWithIds
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSepas() {
    JsonArray result = new JsonArray();
    List<DataProcessorDescription> sesameSepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();

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
    List<DataSinkDescription> sesameSecs = StorageManager.INSTANCE.getStorageAPI().getAllSECs();

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

    try {
      DataProcessorDescription dataProcessorDescription = new DataProcessorDescription(StorageManager.INSTANCE.getStorageAPI().getSEPAById(sepaId));
      return ok(dataProcessorDescription);
    } catch (URISyntaxException e) {
      return ok(Notifications.error("Error"));
    }
  }

  @Path("/actions/{actionId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAction(@PathParam("actionId") String actionId, @QueryParam("keepIds") boolean keepIds) {
    try {
      DataSinkDescription dataSinkDescription = new DataSinkDescription(StorageManager.INSTANCE.getStorageAPI().getSECById(actionId));
      return ok(dataSinkDescription);
    } catch (URISyntaxException e) {
      return ok(Notifications.error("Error"));
    }
  }

  private JsonObject getHeader(String uri, String name) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("uri", new JsonPrimitive(uri));
    jsonObj.add("name", new JsonPrimitive(name));

    return jsonObj;
  }

  private Gson getGson(boolean keepIds) {
    if (keepIds) {
      return GsonSerializer.getGsonWithIds();
    } else {
      return GsonSerializer.getGsonWithoutIds();
    }
  }


}
