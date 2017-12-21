package org.streampipes.rest.serializer;

import com.google.gson.Gson;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.serializers.json.Utils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@GsonWithIds
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonWithIdProvider extends GsonJerseyProvider {

    @Override
    protected Gson getGsonSerializer() {
        return Utils.getGson();
    }
}
