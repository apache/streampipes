package org.streampipes.rest.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.ILogs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.Map;

@Path("/v1/logs")
public class Logs extends AbstractRestInterface implements ILogs {

    static Logger LOG = LoggerFactory.getLogger(Logs.class);

    @POST
    @Path("/pipeline")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllByPipelineId(String pipelineId) {
        String url = BackendConfig.INSTANCE.getElasticsearchURL() + "/" + "logstash-*" +"/_search";
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(url)
                    .header("accept", "application/json")
                    .body("{\"query\": {\"match_phrase\" : {\"correspondingPipeline\" : \"" + pipelineId + "\"}}}")
                    .asJson();
            String respones = jsonResponse.getBody().getObject().toString();
            String hits = extractHits(respones);
            LOG.info("Returned logs for pipeline:" + pipelineId);
            return Response.ok(hits).build();
        } catch (UnirestException e) {
            LOG.error(e.toString());
            return Response.serverError().build();
        }
     }

    @POST
    @Path("/pe")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllByPeuri(String peuri) {
        String url = BackendConfig.INSTANCE.getElasticsearchURL() + "/" + "logstash-*" +"/_search";
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(url)
                    .header("accept", "application/json")
                    .body("{\"query\": {\"match_phrase\" : {\"peURI\" : \"" + peuri + "\"}}}")
                    .asJson();
            String respones = jsonResponse.getBody().getObject().toString();
            String hits = extractHits(respones);
            LOG.info("Returned logs for peuri:" + peuri);
            return Response.ok(hits).build();
        } catch (UnirestException e) {
            LOG.error(e.toString());
            return Response.serverError().build();
        }
    }

    private String extractHits(String response) {
        Gson gson =  new Gson();
        Type stringStringMap = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String,Object> map  = gson.fromJson(response, stringStringMap);
        String json = gson.toJson(map.get("hits"));
        return json;
    }
}
