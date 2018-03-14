package org.streampipes.rest.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.ILogs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v2/logs")
public class Logs extends AbstractRestInterface implements ILogs {

    @GET
    @Path("/{pipelineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllByPipelineId(String pipelineId) {
        //TODO: elastissearch host
        //String url = ElasticsearchConfig.INSTANCE.getElasticsearchURL() + "/" + index +"/_search";
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post("localhost:9200" + "logstash-*" + "/_search")
                    .header("accept", "application/json")
                    .body("{\"query\": {\"match\" : {\"correspondingPipeline\" : \"" + pipelineId + "\"}}}")
                    .asJson();
            String respones = jsonResponse.getBody().getObject().toString();
            return Response.ok(respones).build();
        } catch (UnirestException e) {
            e.printStackTrace();
            return Response.status(500).entity(e).build();
        }
     }

    @GET
    @Path("/{pipelineId}/{peuri}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllByPipelineIdAndPeuri(String pipelineId, String peuri) {
        //TODO: elastissearch host
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post("localhost:9200" + "logstash-*" + "/_search")
                    .header("accept", "application/json")
                    .body("{\"query\": {\"bool\" : {\"must\": ["
                            + " {\"match\"} : { \"correspondingPipeline\" : \"" + pipelineId + "\"}},"
                            + " {\"match\"} : { \"peURI\" : \"" + peuri + "\"}}"
                            +" ]}}}")
                    .asJson();
            String respones = jsonResponse.getBody().getObject().toString();
            return Response.ok(respones).build();
        } catch (UnirestException e) {
            e.printStackTrace();
            return Response.status(500).entity(e).build();
        }
    }
}
