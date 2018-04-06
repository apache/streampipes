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
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.logging.model.Log;
import org.streampipes.logging.model.LogRequest;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.ILogs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

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

        LinkedList logs = new LinkedList();

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(   BackendConfig.INSTANCE.getElasticsearchHost(),
                                        BackendConfig.INSTANCE.getElasticsearchPort(),
                                        BackendConfig.INSTANCE.getElasticsearchProtocol())));

        SearchRequest searchRequest = new SearchRequest("logstash-*");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        searchSourceBuilder.query(boolQuery()
                .must(matchPhraseQuery("logSourceID", logRequest.getsourceID()))
                .must(rangeQuery("time").gte(logRequest.getDateFrom()).lte(logRequest.getDateTo())));

        searchSourceBuilder.size(100);
        searchSourceBuilder.sort(new FieldSortBuilder("time").order(SortOrder.DESC));
        searchSourceBuilder.fetchSource(true);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));


        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest);

            String scrollId = searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();
            Arrays.asList(hits.getHits()).forEach(hit -> logs.add(extractLog(hit)));

            while (hits.getHits().length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(30));
                SearchResponse searchScrollResponse = client.searchScroll(scrollRequest);
                scrollId = searchScrollResponse.getScrollId();
                hits = searchScrollResponse.getHits();
                Arrays.asList(hits.getHits()).forEach(hit -> logs.add(extractLog(hit)));
            }

            ClearScrollRequest request = new ClearScrollRequest();
            request.addScrollId(scrollId);



            client.close();

        } catch (IOException e) {
            LOG.error(e.toString());
            return Response.serverError().build();
        }

        String response = new Gson().toJson(logs);

        return Response.ok(logs).build();
    }


    private Log extractLog(SearchHit hit) {

        Map logMap = hit.getSourceAsMap();

        Log log = new Log();
        log.setTimestamp((String) logMap.get("time"));
        log.setLevel((String) logMap.get("logLevel"));
        log.setsourceID((String) logMap.get("logSourceID"));
        log.setType((String) logMap.get("logType"));
        log.setMessage((String)  logMap.get("logMessage"));

        return log;
    }
}
