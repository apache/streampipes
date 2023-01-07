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

package org.apache.streampipes.ps;

import org.apache.streampipes.dataexplorer.DataLakeManagementV4;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

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

@Path("/v4/datalake/measure")
public class DataLakeMeasureResourceV4 extends AbstractAuthGuardedRestResource {

  private DataLakeManagementV4 dataLakeManagement;

  public DataLakeMeasureResourceV4() {
    this.dataLakeManagement = new DataLakeManagementV4();
  }

  @POST
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/")
  public Response addDataLake(DataLakeMeasure dataLakeMeasure) {
    DataLakeMeasure result = this.dataLakeManagement.addDataLake(dataLakeMeasure);
    return ok(result);
  }

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{id}")
  public Response getDataLakeMeasure(@PathParam("id") String measureId) {
    return ok(this.dataLakeManagement.getById(measureId));
  }

  @PUT
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("{id}")
  public Response updateDataLakeMeasure(@PathParam("id") String measureId,
                                        DataLakeMeasure measure) {
    if (measureId.equals(measure.getElementId())) {
      try {
        this.dataLakeManagement.updateDataLake(measure);
        return ok();
      } catch (IllegalArgumentException e) {
        return badRequest(e.getMessage());
      }
    }
    return badRequest();
  }

  @DELETE
  @JacksonSerialized
  @Path("{id}")
  public Response deleteDataLakeMeasure(@PathParam("id") String measureId) {
    try {
      this.dataLakeManagement.deleteDataLakeMeasure(measureId);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    }
  }
}
