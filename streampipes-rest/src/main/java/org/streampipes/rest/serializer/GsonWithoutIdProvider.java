package org.streampipes.rest.serializer;

import com.google.gson.Gson;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.rest.annotation.GsonWithoutIds;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

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
