package org.streampipes.app.file.export.impl;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.streampipes.app.file.export.api.IElasticsearch;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

@Path("/test")
public class Elasticsearch implements IElasticsearch {

    static String filePath = "files/";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createFiles")
    @Override
    public Response createFiles() {
        //TODO: Use REST Client (Elastic search v5.6+) instead of the TransportClient

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("http://ipe-koi05.fzi.de"), 5601));

            //Index given

            String[] indices = client.admin()
                    .indices()
                    .getIndex(new GetIndexRequest())
                    .actionGet()
                    .getIndices();

            SearchResponse respones = client.prepareSearch(indices)
                    .setPostFilter(QueryBuilders.rangeQuery("timestamp").from(Long.getLong("0")).to(Long.getLong("1506409469751")))
                    .get();

            System.out.print(respones);

            List<String> filePathes = new LinkedList<>();

    /*        filePathes.add("SERVER" + "ID");
            String path = filePath + "ID";
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write("dddd");
            fileWriter.flush();
            fileWriter.close();
*/
            //Create File
            //Create List of file pathes
            //Return this list

            client.close();

            return Response.ok(filePathes).build();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return Response.status(404).entity("Elasticsearch not found!").build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response getFile(String fileId) {
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/download")
    //@Path("/{fileId}/download")
    //public Response getFile(@PathParam("fileId") String fileId) {
    public Response getFile() {
        System.out.print("++++++++++++++++++++++++++++++++++++++++++++");
        File file =  new File(filePath);
        if(file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .build();
        } else {
            return Response.status(404).entity("File not found").build();
        }

    }
}
