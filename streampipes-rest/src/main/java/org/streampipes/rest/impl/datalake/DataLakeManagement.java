/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.rest.impl.datalake;

import org.streampipes.rest.impl.datalake.model.DataResult;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.streampipes.rest.impl.datalake.model.InfoResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataLakeManagement {

    @Deprecated
    public static List<Map<String, Object>> getData() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("ipe-girlitz.fzi.de", 9200, "http")));

        SearchRequest searchRequest = new SearchRequest("sp_shake1");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }


        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            System.out.println(hit.getSourceAsMap());
            result.add(hit.getSourceAsMap());
            // do something with the SearchHit
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Deprecated
    public static void main(String... args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("ipe-girlitz.fzi.de", 9200, "http")));

        SearchRequest searchRequest = new SearchRequest("sp_flow");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);


        System.out.println(searchResponse.getHits().getTotalHits());
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        for (SearchHit hit : searchHits) {
            System.out.println(hit.getSourceAsMap());
            // do something with the SearchHit
        }

        client.close();

    }


    public DataResult getEvents(String index) {
        return null;
    }

    public DataResult getEvents(String index, long from, long to) {
        return null;
    }

    public InfoResult getInfo(String index) {
        return null;
    }

    public List<InfoResult> getAllInfos() {
        return null;
    }

}
