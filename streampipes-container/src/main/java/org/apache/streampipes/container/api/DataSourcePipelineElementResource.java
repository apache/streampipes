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

package org.apache.streampipes.container.api;

import org.apache.streampipes.container.assets.AssetZipGenerator;
import org.apache.streampipes.container.declarer.DataSetDeclarer;
import org.apache.streampipes.container.declarer.DataStreamDeclarer;
import org.apache.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.init.RunningDatasetInstances;
import org.apache.streampipes.container.transform.Transformer;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.vocabulary.StreamPipes;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Path("/sep")
public class DataSourcePipelineElementResource extends AbstractPipelineElementResource<SemanticEventProducerDeclarer> {

  @Override
  protected Map<String, SemanticEventProducerDeclarer> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getProducerDeclarers();
  }

  @GET
  @Path("{sourceId}/{streamId}")
  @Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
  public javax.ws.rs.core.Response getDescription(@PathParam("sourceId") String sourceId, @PathParam("streamId") String streamId) {
    Optional<SpDataStream> stream = getStreamBySourceId(sourceId, streamId);
    if (stream.isPresent()) {
      return ok(prepareElement(stream.get(), getById(sourceId).getUri()));
    } else {
      return clientError();
    }
  }

  @GET
  @Path("{sourceId}/{streamId}/assets")
  @Produces("application/zip")
  public javax.ws.rs.core.Response getAssets(@PathParam("sourceId") String sourceId, @PathParam
          ("streamId") String streamId) {
    try {
      return ok(new AssetZipGenerator(streamId,
                      getStreamBySourceId(sourceId, streamId).get()
                      .getIncludedAssets()).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return javax.ws.rs.core.Response.status(500).build();
    }
  }

  private Optional<SpDataStream> getStreamBySourceId(String sourceId, String streamId) {
    DataSourceDescription dataSourceDescription = (DataSourceDescription) getById(sourceId);
    return dataSourceDescription.getSpDataStreams().stream().filter(ds -> ds.getElementId().equals(streamId))
            .findFirst();
  }

  @POST
  @Path("{sourceId}/{streamId}")
  @Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
  @Consumes({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
  public javax.ws.rs.core.Response invokeRuntime(@PathParam("sourceId") String sourceId, @PathParam("streamId") String streamId, String
          payload) {
    SemanticEventProducerDeclarer declarer = getDeclarerById(sourceId);

    Optional<DataStreamDeclarer> streamDeclarer = declarer
            .getEventStreams()
            .stream()
            .filter(sd -> sd.declareModel(declarer
                    .declareModel())
                    .getElementId()
                    .equals(streamId))
            .findFirst();

    if (streamDeclarer.isPresent()) {
      try {
        SpDataSet dataSet = Transformer.fromJsonLd(SpDataSet.class, payload, StreamPipes.DATA_SET);
        String runningInstanceId = dataSet.getDatasetInvocationId();
        RunningDatasetInstances.INSTANCE.add(runningInstanceId, dataSet, (DataSetDeclarer) streamDeclarer.get().getClass().newInstance());
        RunningDatasetInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(dataSet, ()
                -> {
          // TODO notify
        });
        return ok(new Response(runningInstanceId, true));
      } catch (RDFParseException | RepositoryException | IOException | InstantiationException |
              IllegalAccessException e) {
        e.printStackTrace();
        return ok(new Response("", false, e.getMessage()));
      }
    }
    return ok(new Response("", false, "Could not find the element with id: " + ""));

  }

  @DELETE
  @Path("{sourceId}/{streamId}/{runningInstanceId}")
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

    return ok(new Response(runningInstanceId, false, "Could not find the running instance with id: " + runningInstanceId));
  }
}
