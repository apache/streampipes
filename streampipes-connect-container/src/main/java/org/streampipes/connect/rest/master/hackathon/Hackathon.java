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

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.connect.rest.master.DescriptionResource;
import org.streampipes.connect.rest.master.hackathon.model.Prediction;
import org.streampipes.connect.rest.master.hackathon.model.ResultObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/hackathon")
public class Hackathon extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(DescriptionResource.class);

    @GET
    @Path("/what/{item}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response what(@PathParam("item") String item) {

        logger.info("/what: endpoint with item : " + item + " was called");

        if (HackathonState.INSTANCE.getState() != null) {
            for (Prediction p : HackathonState.INSTANCE.getState().getPredictions()) {
                if (p.getTagName().equals(item)) {
                    logger.info("/what: found item : " + item + " in state with probablility: " + p.getProbability());

                    if (p.getProbability() > 0.1) {
                        return ok("{\"result\": \" " + item +" is in the living room\"}");
                    }
                }

            }
        } else {
            logger.error("/what: oh noes state is null. This should not happen");
        }

        // is in the living room
        // is not in the living room
        return ok("{\"result\": \"" + item + " not found\"}");
    }

    @POST
    @Path("/where")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response where(String s) {
        logger.info("/where: endpoint with event : " + s + " was called");

        ResultObject targetObject = new Gson().fromJson(s, ResultObject.class);
        if (targetObject == null) {
            logger.error("/where: could not deserialize request payload");
        }
        HackathonState.INSTANCE.setState(targetObject);

        logger.info("/where: update state");

        return ok("{\"result\": " + s + "}");
    }

}
