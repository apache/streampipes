package org.streampipes.rest.serializer;

import com.google.gson.Gson;
import org.streampipes.rest.annotation.GsonClientModel;
import org.streampipes.serializers.json.Utils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

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
