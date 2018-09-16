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

package org.streampipes.connect.rest.master.hackathon;

import org.streampipes.connect.rest.AbstractContainerResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/hackathon")
public class Hackathon extends AbstractContainerResource {

    @GET
    @Path("/what/{item}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response what(@PathParam("item") String item) {
        // is in the living room
        // is not in the living room
        return ok("{\"result\": \"is in the living room\"}");
    }

    @POST
    @Path("/where")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response where(String s) {
        System.out.println(s);
        return ok("{\"result\": " + s + "}");
    }

}
