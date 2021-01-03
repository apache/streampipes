/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.rest.impl;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.logging.model.Log;
import org.apache.streampipes.logging.model.LogRequest;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Path("/v2/logs")
public class StreamPipesLogs extends AbstractRestInterface {

    static Logger LOG = LoggerFactory.getLogger(StreamPipesLogs.class);

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response getLogs(LogRequest logRequest) {
        LinkedList logs = new LinkedList();

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(   BackendConfig.INSTANCE.getElasticsearchHost(),
                                        BackendConfig.INSTANCE.getElasticsearchPort(),
                                        BackendConfig.INSTANCE.getElasticsearchProtocol())));

        SearchRequest searchRequest = new SearchRequest("sp_*");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(boolQuery()
                .must(matchPhraseQuery("logSourceID", logRequest.getSourceID()))
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
        log.setLevel((String) logMap.get("logLevel"));
        log.setsourceID((String) logMap.get("logSourceID"));
        log.setType((String) logMap.get("logType"));
        log.setMessage((String)  logMap.get("logMessage"));

        String tmpTimestamp = (String) logMap.get("time");
        tmpTimestamp = tmpTimestamp.substring(0,22);
        try {
            Date date = formatter.parse(tmpTimestamp);
            log.setTimestamp(date.toString().replace("CEST", ""));
        } catch (ParseException e) {
            LOG.error(e.toString());
            log.setLevel(tmpTimestamp);
        }

        return log;
    }
}
