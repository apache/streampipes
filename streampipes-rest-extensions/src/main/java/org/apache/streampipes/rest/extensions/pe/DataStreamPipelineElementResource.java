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

package org.apache.streampipes.rest.extensions.pe;

import org.apache.streampipes.extensions.api.declarer.DataSetDeclarer;
import org.apache.streampipes.extensions.api.declarer.DataStreamDeclarer;
import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningDatasetInstances;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.rest.extensions.AbstractPipelineElementResource;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServicePathPrefix;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Map;

@Path(SpServicePathPrefix.DATA_STREAM)
public class DataStreamPipelineElementResource extends AbstractPipelineElementResource<DataStreamDeclarer> {

  @Override
  protected Map<String, DataStreamDeclarer> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getStreamDeclarers();
  }

  @GET
  @Path("{streamId}/assets")
  @Produces("application/zip")
  public jakarta.ws.rs.core.Response getAssets(@PathParam("streamId") String streamId) {
    try {
      return ok(new AssetZipGenerator(streamId,
          getById(streamId)
              .getIncludedAssets()).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return jakarta.ws.rs.core.Response.status(500).build();
    }
  }

  @POST
  @Path("{streamId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public jakarta.ws.rs.core.Response invokeRuntime(@PathParam("streamId") String streamId, String
      payload) {
    DataStreamDeclarer streamDeclarer = getDeclarerById(streamId);

    try {
      SpDataSet dataSet = JacksonSerializer.getObjectMapper().readValue(payload, SpDataSet.class);
      String runningInstanceId = dataSet.getDatasetInvocationId();
      RunningDatasetInstances.INSTANCE.add(runningInstanceId, dataSet,
          (DataSetDeclarer) streamDeclarer.getClass().newInstance());
      RunningDatasetInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(dataSet, ()
          -> {
        // TODO notify
      });
      return ok(new Response(runningInstanceId, true));
    } catch (IOException | InstantiationException
             | IllegalAccessException e) {
      e.printStackTrace();
      return ok(new Response("", false, e.getMessage()));
    }

    //return ok(new Response("", false, "Could not find the element with id: " + ""));

  }

  @DELETE
  @Path("/{streamId}/{runningInstanceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public jakarta.ws.rs.core.Response detach(@PathParam("runningInstanceId") String runningInstanceId) {

    DataSetDeclarer runningInstance = RunningDatasetInstances.INSTANCE.getInvocation(runningInstanceId);

    if (runningInstance != null) {
      boolean detachSuccess = runningInstance.detachRuntime(runningInstanceId);
      Response resp = new Response("", detachSuccess);
      if (resp.isSuccess()) {
        RunningDatasetInstances.INSTANCE.remove(runningInstanceId);
      }

      return ok(resp);
    }

    return ok(
        new Response(runningInstanceId, false, "Could not find the running instance with id: " + runningInstanceId));
  }
}
