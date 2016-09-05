package de.fzi.cep.sepa.rest.serializer;

import com.google.gson.Gson;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.rest.annotation.GsonWithoutIds;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * Created by riemer on 30.08.2016.
 */
@Provider
@GsonWithoutIds
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonWithoutIdProvider extends GsonJerseyProvider {

    @Override
    protected Gson getGsonSerializer() {
        return GsonSerializer.getGsonWithIds();
    }
}
