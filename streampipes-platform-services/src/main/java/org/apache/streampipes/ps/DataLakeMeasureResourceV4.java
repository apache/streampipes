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
import org.apache.streampipes.dataexplorer.DataLakeNoUserManagementV3;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
}
