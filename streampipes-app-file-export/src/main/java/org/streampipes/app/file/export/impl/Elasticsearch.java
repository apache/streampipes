package org.streampipes.app.file.export.impl;

import com.google.gson.JsonObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.lightcouch.CouchDbClient;
import org.streampipes.app.file.export.api.IElasticsearch;
import org.streampipes.storage.util.Utils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Path("/v1/elasticsearch")
public class Elasticsearch implements IElasticsearch {

    static String mainFilePath = "files/";
    static String serverPath = "streampipes-backend/api/elasticsearch/";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createfiles")
    @Override
    public Response createFiles(String index, long timestampFrom, long timeStampTo) {
        //TODO: Use REST Client (Elastic search v5.6+) instead of the TransportClient

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("http://ipe-koi05.fzi.de"), 5601));

            SearchResponse respones = client.prepareSearch(index)
                    .setPostFilter(QueryBuilders.rangeQuery("timestamp").from(timestampFrom).to(timeStampTo))
                    .get();

            //Time created in milli sec, index, from, to
            String fileName = System.currentTimeMillis() + "-" + index + "-" + timestampFrom + "-" + timeStampTo + ".JSON";
            String filePath = mainFilePath + "sdf" + fileName;
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(respones.toString());
            fileWriter.flush();
            fileWriter.close();

            client.close();

            String endpoint = serverPath + fileName + "/download";

            CouchDbClient couchDbClient = getCouchDbClient();
            couchDbClient.save(endpoint);

            return Response.ok(endpoint).build();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return Response.status(404).entity("Elasticsearch not found!").build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{fileName}/download")
    public Response getFile(@PathParam("fileName") String fileName) {
        File file = new File(mainFilePath + fileName);
        if(file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        } else {
            return Response.status(404).entity("File not found").build();
        }
    }

    @DELETE
    @Path("/{fileName}/delete")
    @Override
    public Response deleteFile(@PathParam("fileName") String fileName) {
        CouchDbClient couchDbClient = getCouchDbClient();
        couchDbClient.remove(serverPath + fileName);

        File file = new File(mainFilePath + fileName);
        file.delete();

        return Response.ok().build();
    }

    @GET
    @Path("/endpoints")
    @Override
    public Response getEndpoints() {
        CouchDbClient couchDbClient = getCouchDbClient();
        List<JsonObject> endpoints = couchDbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);
        return Response.ok(endpoints).build();
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok().build();
    }

    private CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbElasticsearchFilesEndppointClient();
    }
}
