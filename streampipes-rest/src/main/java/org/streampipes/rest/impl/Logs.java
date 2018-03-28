/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
import org.streampipes.logging.model.LogRequest;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.ILogs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.Map;

@Path("/v2/logs")
public class Logs extends AbstractRestInterface implements ILogs {

    static Logger LOG = LoggerFactory.getLogger(Logs.class);


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getLogs(LogRequest logRequest) {
        String url = BackendConfig.INSTANCE.getElasticsearchURL() + "/" + "logstash-*" +"/_search";
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(url)
                    .header("accept", "application/json")
                    .body("GET logstash-*/_search\n" +
                            "{\n" +
                            "  \"query\": {\n" +
                            "    \"bool\": {\n" +
                            "      \"must\": [\n" +
                            "        {\"match_phrase\" : \n" +
                            "    {\"logsource\" : \"" + logRequest.getsource()  + "\"}\n" +
                            "  },\n" +
                            "        {\n" +
                            "          \"range\" : {\n" +
                            "            \"time\": {\"gte\" :" + logRequest.getDateTo() + ",\"lte\" :" + logRequest.getDateFrom() + "}\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  }\n" +
                            "}")
                    .asJson();
            String respones = jsonResponse.getBody().getObject().toString();
            String hits = extractHits(respones);
            LOG.info("Returned logs for logsource:" + logRequest.getsource());
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
