/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.rest.impl.datalake;


import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.rest.impl.datalake.model.DataResult;
import org.streampipes.rest.impl.datalake.model.InfoResult;
import org.streampipes.rest.shared.annotation.GsonWithIds;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/v2/users/{username}/datalake")
public class DataLakeResource extends AbstractRestInterface {
    private DataLakeManagement dataLakeManagement;

    public DataLakeResource() {
        this.dataLakeManagement = new DataLakeManagement();
    }

    public DataLakeResource(DataLakeManagement dataLakeManagement) {
        this.dataLakeManagement = dataLakeManagement;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Path("/data/{index}")
    public Response getAllData(@PathParam("index") String index) {

        DataResult result = this.dataLakeManagement.getEvents(index);

        return Response.ok(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    // TODO add parameters
    @Path("/data/{index}/from/to")
    public Response getDataFromTo(@PathParam("index") String index) {
        DataResult result = this.dataLakeManagement.getEvents(index, 0, 0);
        return Response.ok(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Path("/info/{index}")
    public Response getInfo(@PathParam("index") String index) {
        InfoResult result = this.dataLakeManagement.getInfo(index);

        return Response.ok(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Path("/info")
    public Response getAllInfos() {
        List<InfoResult> result = this.dataLakeManagement.getAllInfos();

        return Response.ok(result).build();
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response getAllData() {

        return Response.ok(DataLakeManagement.getData()).build();

    }

}
