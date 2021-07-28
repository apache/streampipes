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
package org.apache.streampipes.rest.impl.datalake;

import org.apache.streampipes.model.datalake.PersistedDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.rest.impl.dashboard.AbstractPipelineExtractionResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/v3/users/{username}/datalake/pipelines")
public class PersistedDataStreamResource extends AbstractPipelineExtractionResource<PersistedDataStream> {

  private static final String DataLakeAppId = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String MeasureFieldInternalName = "db_measurement";

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPersistedDataStreams() {
    return ok(extract(new ArrayList<>(), DataLakeAppId));
  }

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{pipelineId}/{measureName}")
  public Response getVisualizablePipelineByPipelineIdAndVisualizationName(@PathParam("pipelineId") String pipelineId,
                                                                          @PathParam("measureName") String measureName) {
    return getPipelineByIdAndFieldValue(DataLakeAppId, pipelineId, measureName);
  }

  @Override
  protected PersistedDataStream convert(Pipeline pipeline, DataSinkInvocation sink) {
    PersistedDataStream ps = new PersistedDataStream();
    ps.setSchema(sink.getInputStreams().get(0).getEventSchema());
    ps.setPipelineId(pipeline.getPipelineId());
    ps.setPipelineName(pipeline.getName());
    ps.setMeasureName(extractFieldValue(sink, MeasureFieldInternalName));

    return ps;
  }

  @Override
  protected boolean matches(PersistedDataStream persistedDataStream, String pipelineId, String fieldValue) {
    return persistedDataStream.getPipelineId().equals(pipelineId) &&
            persistedDataStream.getMeasureName().equals(fieldValue);
  }
}
