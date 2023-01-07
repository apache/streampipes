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

import org.apache.streampipes.dataexplorer.DataLakeNoUserManagementV3;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v3/datalake/measure")
@Deprecated
public class DataLakeMeasureResourceV3 extends AbstractAuthGuardedRestResource {

  private DataLakeNoUserManagementV3 dataLakeManagement;

  public DataLakeMeasureResourceV3() {
    this.dataLakeManagement = new DataLakeNoUserManagementV3();
  }

  @POST
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{measure}")
  public Response addDataLake(@PathParam("measure") String measure, EventSchema eventSchema) {
    if (this.dataLakeManagement.addDataLake(measure, eventSchema)) {
      return ok();
    } else {
      return Response.status(409).build();
    }

  }
}
