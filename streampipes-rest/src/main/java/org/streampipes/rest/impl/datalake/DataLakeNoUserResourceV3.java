/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.rest.impl.datalake;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.rest.shared.annotation.GsonWithIds;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/v3/noauth/datalake")
public class DataLakeNoUserResourceV3 extends AbstractRestInterface {

    private DataLakeNoUserManagementV3 dataLakeManagement;

    public DataLakeNoUserResourceV3() {
        this.dataLakeManagement = new DataLakeNoUserManagementV3();
    }

    public DataLakeNoUserResourceV3(DataLakeNoUserManagementV3 dataLakeManagement) {
        this.dataLakeManagement = dataLakeManagement;
    }

    @POST
    @GsonWithIds
    @Path("/{measure}")
    public Response addDataLake(@PathParam("measure") String measure, EventSchema eventSchema) {
        if (this.dataLakeManagement.addDataLake(measure, eventSchema)) {
            return ok();
        } else {
            return Response.status(409).build();
        }

    }
}
