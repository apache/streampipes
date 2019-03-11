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

package org.streampipes.container.api;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.streampipes.container.assets.AssetZipGenerator;
import org.streampipes.container.declarer.DataSetDeclarer;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.init.RunningDatasetInstances;
import org.streampipes.container.transform.Transformer;
import org.streampipes.container.util.Util;
import org.streampipes.model.Response;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.StreamPipes;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/sep")
public class SepElement extends Element<SemanticEventProducerDeclarer> {

  @Override
  protected Map<String, SemanticEventProducerDeclarer> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getProducerDeclarers();
  }

  @GET
  @Path("{sourceId}/{streamId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String getDescription(@PathParam("sourceId") String sourceId, @PathParam("streamId") String streamId) {
    Optional<SpDataStream> stream = getStreamBySourceId(sourceId, streamId);
    if (stream.isPresent()) {
      return getJsonLd(stream.get(), getById(sourceId).getUri());
    } else {
      return "{}";
    }
  }

  @GET
  @Path("{sourceId}/{streamId}/assets")
  @Produces("application/zip")
  public javax.ws.rs.core.Response getAssets(@PathParam("sourceId") String sourceId, @PathParam
          ("streamId") String streamId) {
    return javax.ws.rs.core.Response
            .ok()
            .entity(new AssetZipGenerator(streamId).makeZip())
            .build();
  }

  private Optional<SpDataStream> getStreamBySourceId(String sourceId, String streamId) {
    DataSourceDescription dataSourceDescription = (DataSourceDescription) getById(sourceId);
    return dataSourceDescription.getSpDataStreams().stream().filter(ds -> ds.getElementId().equals(streamId))
            .findFirst();
  }

  @POST
  @Path("{sourceId}/{streamId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String invokeRuntime(@PathParam("sourceId") String sourceId, @PathParam("streamId") String streamId, String
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
        boolean success = RunningDatasetInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(dataSet, ()
                -> {
          // TODO notify
        });
        return Util.toResponseString(new Response(runningInstanceId, true));
      } catch (RDFParseException | RepositoryException | IOException | InstantiationException |
              IllegalAccessException e) {
        e.printStackTrace();
        return Util.toResponseString(new Response("", false, e.getMessage()));
      }
    }
    return Util.toResponseString("", false, "Could not find the element with id: " + "");

  }

  @DELETE
  @Path("{sourceId}/{streamId}/{runningInstanceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public String detach(@PathParam("runningInstanceId") String runningInstanceId) {

    DataSetDeclarer runningInstance = RunningDatasetInstances.INSTANCE.getInvocation(runningInstanceId);

    if (runningInstance != null) {
      boolean detachSuccess = runningInstance.detachRuntime(runningInstanceId);
      Response resp = new Response("", detachSuccess);
      if (resp.isSuccess()) {
        RunningDatasetInstances.INSTANCE.remove(runningInstanceId);
      }

      return Util.toResponseString(resp);
    }

    return Util.toResponseString(runningInstanceId, false, "Could not find the running instance with id: " + runningInstanceId);
  }
}
