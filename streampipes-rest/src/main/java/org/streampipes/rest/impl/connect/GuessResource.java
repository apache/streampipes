package org.streampipes.rest.impl.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.Utils;
import org.streampipes.connect.firstconnector.Adapter;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


@Path("/v2/guess")
public class GuessResource extends AbstractRestInterface {

    Logger logger = LoggerFactory.getLogger(GuessResource.class);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/format")
    public Response guessFormat() {
        //TODO
        return ok(true);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/formatdescription")
    public Response guessFormatDescription() {
        //TODO
        return ok(true);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/schema")
    public Response guessSchema(String ar) {

        System.out.println(ar);


        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        AdapterDescription a = null;
        try {
            a = jsonLdTransformer.fromJsonLd(ar, AdapterDescription.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Adapter adapter = new Adapter("ipe-koi06.fzi.de:9092", "org.streampipes.streamconnect", true);
        EventSchema resultSchema = adapter.getSchema(a);

        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(resultSchema));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        }


        return ok(result);
    }


}

