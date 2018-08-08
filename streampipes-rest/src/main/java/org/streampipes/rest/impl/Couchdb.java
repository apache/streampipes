package org.streampipes.rest.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.streampipes.rest.shared.serializer.annotation.GsonWithIds;
import org.streampipes.rest.api.ICouchdb;
import org.streampipes.storage.couchdb.utils.Utils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v2/couchdb/{table}")
public class Couchdb extends AbstractRestInterface implements ICouchdb {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllData(@PathParam("table") String table) {
        CouchDbClient couchDbClient = Utils.getCoucbDbClient(table);

        List<JsonObject> result = couchDbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);
        String json = new Gson().toJson(result);

        couchDbClient.shutdown();

        return Response.ok(json).build();

    }
}
