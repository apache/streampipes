package de.fzi.cep.sepa.rest.serializer;

import com.google.gson.Gson;
import de.fzi.cep.sepa.rest.annotation.GsonClientModel;
import de.fzi.cep.sepa.model.client.util.Utils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * Created by riemer on 30.08.2016.
 */
@Provider
@GsonClientModel
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonClientModelProvider extends GsonJerseyProvider {

    @Override
    protected Gson getGsonSerializer() {
        return Utils.getGson();
    }
}
