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

import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.declarer.DataSetDeclarer;
import org.apache.streampipes.extensions.management.declarer.DataStreamDeclarer;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningDatasetInstances;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.rest.extensions.AbstractPipelineElementResource;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServicePathPrefix;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
  public javax.ws.rs.core.Response getAssets(@PathParam("streamId") String streamId) {
    try {
      return ok(new AssetZipGenerator(streamId,
          getById(streamId)
              .getIncludedAssets()).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return javax.ws.rs.core.Response.status(500).build();
    }
  }

  @POST
  @Path("{streamId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response invokeRuntime(@PathParam("streamId") String streamId, String
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
  public javax.ws.rs.core.Response detach(@PathParam("runningInstanceId") String runningInstanceId) {

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
