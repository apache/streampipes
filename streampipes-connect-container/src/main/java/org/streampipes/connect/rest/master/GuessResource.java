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

package org.streampipes.connect.rest.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.rest.shared.util.SpMediaType;
import org.streampipes.serializers.jsonld.JsonLdTransformer;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;


@Path("/api/v1/{username}/master/guess")
public class GuessResource extends AbstractContainerResource {

    Logger logger = LoggerFactory.getLogger(GuessResource.class);


    public GuessResource() {

    }
    @GET
    @Produces(SpMediaType.JSONLD)
    @Path("/format")
    public Response guessFormat() {
        //TODO
        return ok(true);
    }


    @GET
    @Produces(SpMediaType.JSONLD)
    @Path("/formatdescription")
    public Response guessFormatDescription() {
        //TODO
        return ok(true);
    }

    @POST
    @Produces(SpMediaType.JSONLD)
    @Path("/schema")
//    public Response guessSchema(String ar, @PathParam("username") String userName) {
        public Response guessSchema(@PathParam("username") String userName) {


//        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
//
//        AdapterDescription a = null;
//        try {
//            if (ar.contains("AdapterSetDescription")){
//                a = jsonLdTransformer.fromJsonLd(ar, AdapterSetDescription.class);
//            } else {
//                a = jsonLdTransformer.fromJsonLd(ar, AdapterStreamDescription.class);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Adapter adapter = new Adapter("ipe-koi06.fzi.de:9092", "org.streampipes.streamconnect", true);
//        GuessSchema resultSchema = adapter.getSchema(a);

        // TODO get domainproperty probabilities

//        return ok(JsonLdUtils.toJsonLD(resultSchema));

        EventSchema eventSchema = new EventSchema();
        EventPropertyPrimitive eventPropertyPrimitive = new EventPropertyPrimitive();
        eventPropertyPrimitive.setRuntimeType("http://schema.org/Number");
        eventPropertyPrimitive.setRuntimeName("id");


        eventSchema.setEventProperties(Arrays.asList(eventPropertyPrimitive));
        GuessSchema guessSchema = new GuessSchema();
        guessSchema.setEventSchema(eventSchema);


        return ok(JsonLdUtils.toJsonLD(guessSchema));
    }


}

